package gcboard.gcboardrefactoring.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GcBoardBusinessException extends GcBoardException {

    public GcBoardBusinessException(String message, HttpStatus status) {
        super(message, status);
    }
}
