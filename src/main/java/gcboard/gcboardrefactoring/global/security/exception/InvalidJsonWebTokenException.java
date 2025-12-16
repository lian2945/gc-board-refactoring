package gcboard.gcboardrefactoring.global.security.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class InvalidJsonWebTokenException extends GcBoardBusinessException {
    public InvalidJsonWebTokenException() {
        super("올바르지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
    }
}
