package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class CommentAcceptDepthException extends GcBoardBusinessException {
    public CommentAcceptDepthException() {
        super("대댓글은 대표 댓글로 지정할 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
