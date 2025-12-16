package gcboard.gcboardrefactoring.domain.comment.presentation.dto.response;

import gcboard.gcboardrefactoring.domain.comment.domain.entity.CommentEntity;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        Long parentId,
        Long rootId,
        Integer depth,
        String content,
        String authorNickname,
        Boolean isDeleted,
        String path,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponseDto from(CommentEntity commentEntity) {
        String content = commentEntity.getIsDeleted() ? "[삭제된 댓글]" : commentEntity.getContent();
        return new CommentResponseDto(
                commentEntity.getId(),
                commentEntity.getPost().getId(),
                commentEntity.getParent() != null ? commentEntity.getParent().getId() : null,
                commentEntity.getRootId(),
                commentEntity.getDepth(),
                content,
                commentEntity.getAuthorNickname(),
                commentEntity.getIsDeleted(),
                commentEntity.getPath(),
                commentEntity.getCreatedAt(),
                commentEntity.getUpdatedAt()
        );
    }
}
