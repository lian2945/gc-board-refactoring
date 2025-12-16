package gcboard.gcboardrefactoring.domain.post.application.service;

import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostLikeEntity;
import gcboard.gcboardrefactoring.domain.post.domain.repository.PostLikeRepository;
import gcboard.gcboardrefactoring.domain.post.domain.repository.PostRepository;
import gcboard.gcboardrefactoring.domain.post.exception.PostAccessDeniedException;
import gcboard.gcboardrefactoring.domain.post.exception.PostNotFoundException;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostCreateRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostSearchRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostLikeResponseDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostResponseDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostSummaryResponseDto;
import gcboard.gcboardrefactoring.global.cursor.request.LastReadCursorRequestDto;
import gcboard.gcboardrefactoring.global.cursor.response.CursorResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import gcboard.gcboardrefactoring.global.validation.ProfanityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final ProfanityValidator profanityValidator;

    @Transactional
    public PostResponseDto createPost(GcBoardUserDetails gcBoardUserDetails, PostCreateRequestDto requestDto) {
        profanityValidator.validateClean(requestDto.title(), requestDto.content());

        PostEntity postEntity = PostEntity.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .authorNickname(gcBoardUserDetails.getUsername())
                .build();

        postRepository.save(postEntity);
        return PostResponseDto.from(postEntity, false);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId, GcBoardUserDetails gcBoardUserDetails) {
        PostEntity postEntity = findPostOrThrow(postId);
        boolean liked = isLiked(postEntity, gcBoardUserDetails);
        return PostResponseDto.from(postEntity, liked);
    }

    @Transactional(readOnly = true)
    public CursorResponseDto<PostSummaryResponseDto> getPosts(LastReadCursorRequestDto cursorRequestDto, GcBoardUserDetails gcBoardUserDetails) {
        LocalDateTime lastReadAt = Optional.ofNullable(cursorRequestDto.lastReadAt()).orElse(LocalDateTime.now());
        long limit = cursorRequestDto.count() + 1;

        String nickname = gcBoardUserDetails == null ? null : gcBoardUserDetails.getUsername();
        List<PostSummaryResponseDto> posts = postRepository.findByCreatedAtBefore(lastReadAt, nickname, limit);
        boolean hasMore = posts.size() > cursorRequestDto.count();

        List<PostSummaryResponseDto> content = hasMore ? posts.subList(0, cursorRequestDto.count().intValue()) : posts;
        return new CursorResponseDto<>(
                content,
                content.size(),
                !hasMore
        );
    }

    @Transactional(readOnly = true)
    public CursorResponseDto<PostSummaryResponseDto> searchPosts(PostSearchRequestDto searchRequestDto, GcBoardUserDetails gcBoardUserDetails) {
        LastReadCursorRequestDto cursor = Optional.ofNullable(searchRequestDto.cursor())
                .orElse(new LastReadCursorRequestDto(LocalDateTime.now(), 20L));
        
        LocalDateTime lastReadAt = Optional.ofNullable(cursor.lastReadAt()).orElse(LocalDateTime.now());
        
        String keyword = searchRequestDto.keyword().trim();
        long limit = cursor.count() + 1;

        String nickname = gcBoardUserDetails == null ? null : gcBoardUserDetails.getUsername();
        List<PostSummaryResponseDto> posts = postRepository.searchByFullText(keyword, lastReadAt, nickname, limit);
        boolean hasMore = posts.size() > cursor.count();
        List<PostSummaryResponseDto> content = hasMore ? posts.subList(0, cursor.count().intValue()) : posts;

        return new CursorResponseDto<>(
                content,
                content.size(),
                !hasMore
        );
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, GcBoardUserDetails gcBoardUserDetails, PostUpdateRequestDto requestDto) {
        profanityValidator.validateClean(requestDto.title(), requestDto.content());

        PostEntity postEntity = findPostOrThrow(postId);
        validateAuthor(postEntity, gcBoardUserDetails.getUsername());
        postEntity.update(requestDto.title(), requestDto.content());
        boolean liked = isLiked(postEntity, gcBoardUserDetails);
        return PostResponseDto.from(postEntity, liked);
    }

    @Transactional
    public void deletePost(Long postId, GcBoardUserDetails gcBoardUserDetails) {
        PostEntity postEntity = findPostOrThrow(postId);
        validateAuthor(postEntity, gcBoardUserDetails.getUsername());
        postRepository.delete(postEntity);
    }

    @Transactional
    public PostLikeResponseDto toggleLike(Long postId, GcBoardUserDetails gcBoardUserDetails) {
        PostEntity postEntity = findPostOrThrow(postId);

        return postLikeRepository.findByPostAndUserNickname(postEntity, gcBoardUserDetails.getUsername())
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    postEntity.decreaseLikeCount();
                    return new PostLikeResponseDto(postEntity.getId(), false, postEntity.getLikeCount());
                })
                .orElseGet(() -> {
                    PostLikeEntity likeEntity = PostLikeEntity.builder()
                            .post(postEntity)
                            .userNickname(gcBoardUserDetails.getUsername())
                            .build();
                    postLikeRepository.save(likeEntity);
                    postEntity.increaseLikeCount();
                    return new PostLikeResponseDto(postEntity.getId(), true, postEntity.getLikeCount());
                });
    }

    private PostEntity findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validateAuthor(PostEntity postEntity, String nickname) {
        if (!postEntity.getAuthorNickname().equals(nickname)) {
            throw new PostAccessDeniedException();
        }
    }

    private boolean isLiked(PostEntity postEntity, GcBoardUserDetails gcBoardUserDetails) {
        if (gcBoardUserDetails == null) {
            return false;
        }
        return postLikeRepository.findByPostAndUserNickname(postEntity, gcBoardUserDetails.getUsername()).isPresent();
    }
}
