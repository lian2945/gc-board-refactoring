# GC-Board Refactoring

> **배포 URL**: https://dongwook.jaehwan.kr
> **테스트 계정**: ID: `testtesttest` / PW: `testtesttest`

## 📌 프로젝트 소개

Spring Boot 기반의 게시판 애플리케이션 리팩토링 프로젝트입니다. 기존 코드의 성능 및 구조적 문제점을 개선하여 더 안정적이고 확장 가능한 시스템으로 재설계하였습니다.

- **개발 기간**: 2025.12.01 ~ 2025.12.18
- **개발 인원**: 1인 (개인 프로젝트)

---

## 🔍 개선 사항

### 기존 코드의 문제점

| 문제점 | 개선 방법 |
|--------|----------|
| DB 인덱스 미적용으로 인한 조회 성능 저하 | 주요 조회 컬럼(created_at, user_id 등)에 인덱스 적용 |
| 일관성 없는 예외 처리 | @RestControllerAdvice를 활용한 전역 예외 처리 구현 |
| 보안 설정 부재 | Spring Security + JWT 기반 인증/인가 구현 |
| 동시성 제어 미흡 | 낙관적 락(Optimistic Lock) 적용 |
| ID 생성 속도 저하 | Atomic 타입과 compareAndSet을 활용한 ID 생성 최적화 |

### 개선 결과

**[개선 1: ID 생성 성능 최적화]**

- **개선 전**: 단순 증가 방식으로 동시성 환경에서 병목 발생
- **개선 후**: AtomicLong과 compareAndSet 메서드를 사용하여 락 없이 안전한 ID 생성
- **성능 개선**: ID 생성 속도 약 **1.5배 향상**

**[개선 2: 전역 예외 처리]**

- **개선 전**: 모든 예외가 500 에러로 반환되어 클라이언트가 원인 파악 불가
- **개선 후**: @RestControllerAdvice를 통해 비즈니스 예외별로 적절한 HTTP 상태 코드(400, 404 등)와 명확한 에러 메시지 제공

**[개선 3: 보안 강화]**

- **개선 전**: 인증/인가 로직 부재
- **개선 후**: Spring Security + JWT를 활용한 Stateless 인증 구현

---

## ✨ 주요 기능

### 1. 사용자 인증
- 회원가입 / 로그인 / 로그아웃
- JWT 토큰 기반 인증
- 이메일 인증

### 2. 게시글 관리
- 게시글 CRUD
- 게시글 좋아요
- 게시글 검색
- **[심화 기술]** 커서 기반 무한 스크롤 (Cursor Pagination)

### 3. 댓글 기능
- 댓글 CRUD
- 대댓글 (계층형 댓글)
- **[심화 기술]** 댓글 채택 기능

### 4. 프로필 관리
- 사용자 프로필 조회
- 프로필 수정

---

## 🛠️ 기술 스택

### Backend
- Java 21
- Spring Boot 4.0.0
- Spring Data JPA
- Spring Security
- MySQL 8.0
- Redis
- QueryDSL 7.1
- JWT (JJWT 0.12.3)
- Spring Mail

### Frontend
- Next.js

### Deployment
- Backend: Proxmox (Self-hosted)
- Frontend: Vercel / Cloudflare Pages
- Database: MySQL on Proxmox

---

## 📂 프로젝트 구조

```
src/main/java/gcboard/gcboardrefactoring/
├── domain/
│   ├── auth/                    # 인증 도메인
│   │   ├── application/
│   │   │   ├── event/          # 이메일 인증 이벤트
│   │   │   ├── listener/       # 이벤트 리스너
│   │   │   └── service/        # 인증 서비스
│   │   ├── exception/          # 인증 예외
│   │   └── presentation/
│   │       └── dto/            # 요청/응답 DTO
│   ├── comment/                # 댓글 도메인
│   │   ├── application/
│   │   │   └── service/
│   │   ├── domain/
│   │   │   ├── entity/         # 댓글 엔티티
│   │   │   └── repository/     # 댓글 리포지토리
│   │   ├── exception/
│   │   └── presentation/
│   │       ├── CommentController.java
│   │       └── dto/
│   ├── post/                   # 게시글 도메인
│   │   ├── application/
│   │   │   └── service/
│   │   ├── domain/
│   │   │   ├── entity/         # 게시글, 좋아요 엔티티
│   │   │   └── repository/
│   │   ├── exception/
│   │   └── presentation/
│   │       ├── PostController.java
│   │       └── dto/
│   └── user/                   # 사용자 도메인
│       ├── application/
│       │   └── service/
│       ├── domain/
│       │   ├── entity/
│       │   ├── enums/          # Role 등
│       │   └── repository/
│       ├── exception/
│       └── presentation/
│           └── controller/
│               └── UserController.java
└── global/                     # 공통 모듈
    ├── configuration/          # 설정 (Security, JPA, Redis 등)
    ├── constants/              # 상수
    ├── cursor/                 # 커서 페이징 유틸
    ├── entity/                 # 공통 엔티티
    ├── exception/              # 전역 예외 처리
    ├── properties/             # 프로퍼티 설정
    └── security/               # Security 필터, JWT 등
        ├── filter/
        ├── jwt/
        └── user/
```

---

## 🔗 API 명세

### 인증

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/email/send` | 이메일 인증 코드 전송 |
| POST | `/api/auth/email/verify` | 이메일 인증 |
| POST | `/api/reissue` | 토큰 재발급 |

### 게시글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/posts?lastReadAt={timestamp}&count={count}` | 게시글 목록 조회 (커서 페이징) |
| GET | `/posts/{postId}` | 게시글 상세 조회 |
| GET | `/posts/search?keyword={keyword}&lastReadAt={timestamp}&count={count}` | 게시글 검색 |
| POST | `/posts` | 게시글 작성 |
| PATCH | `/posts/{postId}` | 게시글 수정 |
| DELETE | `/posts/{postId}` | 게시글 삭제 |
| POST | `/posts/{postId}/likes` | 게시글 좋아요 토글 |

### 댓글

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/posts/{postId}/comments` | 댓글 목록 조회 (계층형) |
| POST | `/posts/{postId}/comments?parent_comment_id={id}` | 댓글 작성 (대댓글 포함) |
| PATCH | `/posts/{postId}/comments/{commentId}` | 댓글 수정 |
| DELETE | `/posts/{postId}/comments/{commentId}` | 댓글 삭제 |
| POST | `/posts/{postId}/comments/{commentId}/accept` | 댓글 채택 |
| DELETE | `/posts/{postId}/comments/{commentId}/accept` | 댓글 채택 취소 |

### 프로필

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/profile` | 내 프로필 조회 |
| GET | `/profile/{nickname}` | 특정 사용자 프로필 조회 |
| PATCH | `/profile` | 프로필 수정 |

---

## 💻 로컬 실행 방법

### 1. 레포지토리 클론
```bash
git clone https://github.com/your-username/gc-board-refactoring.git
cd gc-board-refactoring
```

### 2. 환경 변수 설정

`src/main/resources/application.yml` 파일을 생성하고 다음 설정을 추가합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gcboard
    username: your-username
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your-jwt-secret-key-min-256-bits
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

### 3. 데이터베이스 설정

MySQL 데이터베이스를 생성합니다:

```sql
CREATE DATABASE gcboard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 백엔드 실행

```bash
# Gradle을 사용하여 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/gc-board-refactoring-0.0.1.jar
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 5. 프론트엔드 실행

프론트엔드는 별도 레포지토리에서 관리됩니다. Next.js 프로젝트를 클론하고 다음 명령어로 실행합니다:

```bash
cd frontend
npm install

# .env.local 파일에 API URL 설정
# NEXT_PUBLIC_API_URL=http://localhost:8080

npm run dev
```

---

## 🎥 시연 영상

[YouTube 링크]
https://www.youtube.com/watch?v=EfSraE3_Fw0
---

## 📚 참고 자료

- [Spring Boot 4.0 Documentation](https://docs.spring.io/spring-boot/docs/4.0.0/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/reference/)
- [JWT Introduction](https://jwt.io/introduction)
- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- [QueryDSL Documentation](http://querydsl.com/static/querydsl/latest/reference/html/)
- [Redis Documentation](https://redis.io/docs/)
- [Cursor-based Pagination](https://www.prisma.io/docs/orm/prisma-client/queries/pagination#cursor-based-pagination)
