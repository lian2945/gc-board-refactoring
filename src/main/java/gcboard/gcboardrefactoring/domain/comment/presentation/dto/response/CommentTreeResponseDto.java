package gcboard.gcboardrefactoring.domain.comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record CommentTreeResponseDto(
        @JsonProperty("commentId") String commentId,
        @JsonProperty("postId") String postId,
        @JsonProperty("parentId") String parentId,
        @JsonProperty("rootId") String rootId,
        Integer depth,
        String content,
        String authorNickname,
        Boolean isDeleted,
        String path,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    @QueryProjection
    public CommentTreeResponseDto(Long commentId, Long postId, Long parentId, Long rootId, Integer depth, 
                                   String content, String authorNickname, Boolean isDeleted, String path,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(
            commentId != null ? commentId.toString() : null,
            postId != null ? postId.toString() : null,
            parentId != null ? parentId.toString() : null,
            rootId != null ? rootId.toString() : null,
            depth, content, authorNickname, isDeleted, path, createdAt, updatedAt
        );
    }

    public CommentTreeResponseDto maskDeleted() {
        if (Boolean.TRUE.equals(isDeleted)) {
            return new CommentTreeResponseDto(
                    commentId,
                    postId,
                    parentId,
                    rootId,
                    depth,
                    "[삭제된 댓글]",
                    authorNickname,
                    isDeleted,
                    path,
                    createdAt,
                    updatedAt
            );
        }
        return this;
    }
}
