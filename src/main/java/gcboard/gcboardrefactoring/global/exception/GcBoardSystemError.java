package gcboard.gcboardrefactoring.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GcBoardSystemError extends GcBoardException {

    public GcBoardSystemError(String message, HttpStatus status) {
        super(message, status);
    }
}
