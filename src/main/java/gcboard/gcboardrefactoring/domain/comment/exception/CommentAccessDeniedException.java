package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class CommentAccessDeniedException extends GcBoardBusinessException {
    public CommentAccessDeniedException() {
        super("작성자만 댓글을 수정하거나 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
    }
}
