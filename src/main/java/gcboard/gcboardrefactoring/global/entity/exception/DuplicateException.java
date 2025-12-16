package gcboard.gcboardrefactoring.global.entity.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateException extends GcBoardBusinessException {
    public DuplicateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
