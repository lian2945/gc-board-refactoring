package gcboard.gcboardrefactoring.global.infrastructure.feign.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardSystemError;
import org.springframework.http.HttpStatus;

public class FeignClientResponseException extends GcBoardSystemError {
    public FeignClientResponseException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
