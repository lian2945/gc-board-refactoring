package gcboard.gcboardrefactoring.domain.post.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class PostAcceptAccessDeniedException extends GcBoardBusinessException {
    public PostAcceptAccessDeniedException() {
        super("게시글 작성자만 채택/해제할 수 있습니다.", HttpStatus.FORBIDDEN);
    }
}
