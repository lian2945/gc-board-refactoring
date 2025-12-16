package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class CommentAlreadyDeletedException extends GcBoardBusinessException {
    public CommentAlreadyDeletedException() {
        super("이미 삭제된 댓글입니다.", HttpStatus.BAD_REQUEST);
    }
}
