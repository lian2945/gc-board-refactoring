package gcboard.gcboardrefactoring.domain.post.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class PostAccessDeniedException extends GcBoardBusinessException {
    public PostAccessDeniedException() {
        super("작성자만 수정하거나 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
    }
}
