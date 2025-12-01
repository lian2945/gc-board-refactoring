package gcboard.gcboardrefactoring.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GcBoardException extends RuntimeException {
    private final HttpStatus status;

    public GcBoardException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}