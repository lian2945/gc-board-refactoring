package gcboard.gcboardrefactoring.domain.comment.domain.repository;

import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentTreeResponseDto;

import java.util.List;

public interface CommentQuerydslRepository {
    List<CommentTreeResponseDto> findByPostIdWithCursor(Long postId, String parentPathPrefix, String cursorPath, long limit);
}
