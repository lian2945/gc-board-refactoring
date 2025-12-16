package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class InvalidVerificationCodeException extends GcBoardBusinessException {
    public InvalidVerificationCodeException() {
        super("유효하지 않은 인증 코드입니다.", HttpStatus.BAD_REQUEST);
    }
}
