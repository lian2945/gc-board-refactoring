package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class AlreadyVerifiedEmailException extends GcBoardBusinessException {
    public AlreadyVerifiedEmailException() {
        super("이미 인증된 이메일입니다.", HttpStatus.CONFLICT);
    }
}
