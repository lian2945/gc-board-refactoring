package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class CommentDepthExceededException extends GcBoardBusinessException {
    public CommentDepthExceededException(int maxDepth) {
        super("댓글은 최대 " + maxDepth + "단계까지 가능합니다.", HttpStatus.BAD_REQUEST);
    }
}
