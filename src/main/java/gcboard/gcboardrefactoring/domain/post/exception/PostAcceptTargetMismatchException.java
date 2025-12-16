package gcboard.gcboardrefactoring.domain.post.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class PostAcceptTargetMismatchException extends GcBoardBusinessException {
    public PostAcceptTargetMismatchException() {
        super("채택 대상 댓글이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
    }
}
