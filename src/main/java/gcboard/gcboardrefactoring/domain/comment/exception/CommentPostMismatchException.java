package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class CommentPostMismatchException extends GcBoardBusinessException {
    public CommentPostMismatchException() {
        super("요청한 게시글에 속하지 않은 댓글입니다.", HttpStatus.BAD_REQUEST);
    }
}
