package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class InactiveUserException extends GcBoardBusinessException {
    public InactiveUserException() {
        super("이메일 인증이 필요합니다.", HttpStatus.FORBIDDEN);
    }
}
