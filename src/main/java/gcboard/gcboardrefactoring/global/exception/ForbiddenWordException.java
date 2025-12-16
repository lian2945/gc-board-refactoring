package gcboard.gcboardrefactoring.global.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenWordException extends GcBoardBusinessException {
    public ForbiddenWordException() {
        super("부적절한 단어가 포함되어 있습니다.", HttpStatus.BAD_REQUEST);
    }
}
