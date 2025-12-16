package gcboard.gcboardrefactoring.global.security.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class AuthenticationRequiredException extends GcBoardBusinessException {
    public AuthenticationRequiredException() {
        super("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }
}
