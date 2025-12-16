package gcboard.gcboardrefactoring.domain.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;

import java.time.LocalDateTime;

public record PostResponseDto(
        @JsonProperty("postId") String postId,
        String title,
        String content,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long likeCount,
        @JsonProperty("acceptedCommentId") String acceptedCommentId,
        boolean liked
) {
    public static PostResponseDto from(PostEntity postEntity, boolean liked) {
        return new PostResponseDto(
                postEntity.getId() != null ? postEntity.getId().toString() : null,
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getAuthorNickname(),
                postEntity.getCreatedAt(),
                postEntity.getUpdatedAt(),
                postEntity.getLikeCount(),
                postEntity.getAcceptedCommentId() != null ? postEntity.getAcceptedCommentId().toString() : null,
                liked
        );
    }
}
