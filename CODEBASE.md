# CODEBASE.md

## 사용자/인증 영역 요약
- 사용자 엔터티(`UserEntity` → `gc_user`) 필수 필드: `mail`(수정 불가), `nickname`, `password`, `role`, `is_active`. 프로필 필드는 `profile`, `description`. 기본 역할은 `USER`, 활성 상태는 이메일 코드 검증 시 `true`로 설정된다(빌더 기본값은 `false`). 이메일/닉네임에 DB 인덱스를 추가했다(엔티티 수준 unique 제약은 두지 않음).
- JWT 필터(`GcBoardAuthenticationFilter`)가 `Authorization: Bearer <token>`에서 닉네임을 추출하고 `GcBoardUserDetailsService`가 닉네임으로 사용자를 로드한다. 토큰은 `JwtTokenProvider`가 생성하고, `JwtValidator`가 `made_by` 클레임을 검사한다.
- 프로필 API (`/user`):
  - `GET /user/profile/{nickname}` / `GET /user/profile`: 닉네임 기반 조회. Querydsl 프로젝션으로 필요한 컬럼만 선택.
  - `PATCH /user/profile`: 닉네임/프로필/자기소개 수정. 동일 닉네임일 경우 중복 예외를 방지하도록 검사 개선.
- 인증 API (`/auth`):
  - `POST /auth/email/send`: 입력 이메일로 인증 코드 발급 → Redis(`email:verify:mail:<mail>` / `email:verify:code:<code>`, TTL 15분) 저장 → 이벤트 발행으로 메일 송신. 이미 가입된 이메일이면 거부. 동일 메일 기준 1시간 내 최대 5회 요청 가능(`email:verify:request-count:<mail>` 키, TTL 1시간).
  - `POST /auth/signup`: 요청 필드(`mailVerificationCode`, `nickname`, `password`, `profile?`, `description?`). Redis에 저장된 코드로 이메일을 역조회·검증 후 닉네임/이메일 중복 검사, BCrypt 해싱으로 사용자 생성(`is_active=true`). 완료 후 인증 완료 이벤트 발행.
  - `POST /auth/email/verify`: 코드로 이메일을 역조회해 기존 사용자를 활성화(보조 용도), 인증 완료 이벤트 발행.
  - `POST /auth/login`: 닉네임·비밀번호 검증(미인증 시 거부) 후 Access/Refresh 토큰 발급. 로그인 조회는 Querydsl 프로젝션(`UserLoginQueryDto`)으로 필요한 컬럼만 읽어 I/O 최소화.
- 이벤트/인프라
  - `EmailVerificationRequestedEvent` → `EmailEventListener`가 `JavaMailSender`로 메일 전송(코드/유효 시간 포함). 실패 시 시스템 예외로 승격.
  - `EmailVerifiedEvent`는 인증 완료 로깅용으로 발행.
  - 이메일 인증 코드는 `StringRedisTemplate`를 사용해 메일↔코드 양방향 키로 6자리 숫자/15분 TTL로 저장한다.
- 시큐리티
  - `SecurityConfiguration`에서 `/auth/**`, `/api/reissue`, `/login/oauth2`, `/api/healthcheck`는 허용하고 나머지는 인증 필요. 세션은 STATELESS, JWT 필터/예외 필터 등록.
- 기타
  - JPA 감사(`GcBoardEntity` → `id`, `createdAt`, `updatedAt`) 활성화.
  - Redis 직렬화(`RedisConfiguration`)와 Querydsl `JPAQueryFactory` 제공.

## 게시물 영역
- 엔티티
- `PostEntity`(`post`): `id`(Sonyflake), `title`(<=255, not null), `content`(TEXT, FULLTEXT index 포함), `author_nickname`, `like_count`(기본 0), `accepted_comment_id`(nullable), `created_at`, `updated_at`. 인덱스: `author_nickname`, `created_at`, `content(fulltext)`. `likes` 컬렉션에 `cascade = ALL`, `orphanRemoval = true` 설정해 게시글 삭제 시 좋아요도 함께 제거.
  - Fulltext 인덱스가 없다면 아래 SQL을 실행해 `title`/`content` 기반 검색을 보완하세요.
    ```sql
    ALTER TABLE post ADD FULLTEXT idx_post_fulltext(title, content);
    ```
  - `PostLikeEntity`(`post_like`): `id`(Sonyflake), `post_id`(FK), `user_nickname`, `version`(@Version, 낙관적 락). 유니크 제약: (`post_id`, `user_nickname`). PostLike가 연관관계를 소유한다.
- API (`/posts`):
  - `POST /posts`: 본문 `title`, `content`. 작성자 닉네임은 인증 토큰에서 추출. 생성된 게시글 정보를 반환.
  - `GET /posts/{postId}`: 단건 조회. 존재하지 않으면 404.
  - `GET /posts`: 커서 기반 조회. 쿼리 파라미터 `lastReadAt`(ISO datetime), `count`(1~50) → `created_at < lastReadAt` 순으로 `created_at DESC, id DESC` 정렬 후 반환. 응답은 `CursorResponseDto`(`content`, `size`, `last`).
  - `GET /posts/search`: `keyword` + `lastReadAt`/`count`로 제목/본문 LIKE 검색(현재 Querydsl LIKE, FULLTEXT 쿼리 필요 시 Querydsl native로 교체 가능).
  - `PATCH /posts/{postId}`: 작성자 본인만 수정(권한 없으면 403). `title`, `content` 필수.
  - `DELETE /posts/{postId}`: 작성자 본인만 삭제(권한 없으면 403). 연결된 좋아요는 FK CASCADE.
  - `POST /posts/{postId}/likes`: 토글 방식 좋아요/좋아요 취소. 결과(`postId`, `liked`, `likeCount`) 반환. 좋아요 엔티티의 @Version을 사용.
  - `POST /posts/{postId}/comments/{commentId}/accept`: 게시글 작성자만 채택 설정.
  - `DELETE /posts/{postId}/comments/{commentId}/accept`: 채택 해제. 채택된 댓글과 불일치 시 400.
- 검증/제약: 제목 255자 이하/필수, 내용 필수, 커서 조회 `count` 1~50. 작성/수정/삭제/좋아요/채택은 인증 필요(보안 설정상 기본 인증 요구).

## 댓글(계층형, 무한 뎁스) 설계 메모
- 모델링
  - `CommentEntity`(`comment`): `id`(Sonyflake), `post_id`(FK), `parent_id`(nullable self FK), `root_id`(최상위 댓글 id), `depth`(0부터), `path`(materialized path), `content`, `author_nickname`, `is_deleted`, `created_at`, `updated_at`. 인덱스: `(post_id, path)`, `(post_id, root_id)`, `parent_id`, `author_nickname`.
  - `path`: 16진 8자리 패딩 세그먼트(`/` 구분)로 id를 이어 붙여 정렬 안정성 확보. 정렬은 `path ASC, id ASC`. soft delete 시 content는 “[삭제된 댓글]”로 대체.
- API (`/posts/{postId}/comments`)
  - `POST /comments`: `content` 필수, `parentId?` 선택. 부모가 다른 게시글이면 400. 생성 후 `root_id`/`path`를 id로 세팅.
  - `GET /comments`: 쿼리 `cursorPath?`, `count`(1~50). 조건 `path > cursorPath`(없으면 루트부터), 정렬 `path ASC, id ASC`, 응답 `CursorResponseDto`.
  - `PATCH /comments/{commentId}`: 작성자만, soft delete된 댓글은 400.
  - `DELETE /comments/{commentId}`: 작성자만 soft delete(`is_deleted=true`, content 치환).
- 제약/권한
  - 작성/수정/삭제는 인증 필수. 본문 길이 1~2000자. 부모가 없으면 depth=0/root_id=id/path는 단일 세그먼트.
  - 리스트는 소프트 삭제여도 트리 유지, content만 대체 문자열로 반환한다.
- 채택
  - 단일 채택 정책: 게시글당 한 개의 댓글만 채택 가능. 게시글 작성자만 채택/해제 권한.
  - 데이터: `PostEntity.accepted_comment_id` 컬럼 사용. 채택 시 대상 댓글이 해당 게시글 소속인지 검증. 채택된 댓글이 soft delete되면 서비스에서 자동 해제 처리.
  - API: `POST /posts/{postId}/comments/{commentId}/accept`(채택), `DELETE /posts/{postId}/comments/{commentId}/accept`(해제). 채택된 댓글과 불일치하면 400.
- 금칙어/욕설 필터
  - 공통 `ProfanityValidator`가 게시글 제목/본문, 댓글 본문에 적용된다. 기본 금칙어 목록(`fuck`, `shit`, `bitch`, `씨발`, `욕설` 등)을 소문자 포함 여부로 검사한다.
  - 금칙어가 발견되면 `ForbiddenWordException`(400) 발생. 필요 시 금칙어 목록을 확장하거나 외부 설정으로 뺄 수 있다.

## 유의 사항
- 기존 DB에 사용자 레코드가 있다면 `password`/`is_active` 컬럼 추가 시 마이그레이션이 필요하다(미인증 상태를 false로 두고 비밀번호를 채워 넣어야 함).
- 메일 전송을 위해 `spring.mail.*` 환경 설정이 필요하다. 인증 코드 TTL(10분) 변경은 `EmailVerificationService`의 `DEFAULT_TTL`을 조정하면 된다.
