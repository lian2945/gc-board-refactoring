package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class VerificationCodeExpiredException extends GcBoardBusinessException {
    public VerificationCodeExpiredException() {
        super("인증 코드가 만료되었거나 요청되지 않았습니다.", HttpStatus.BAD_REQUEST);
    }
}
