package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

import java.time.Duration;

public class TooManyEmailVerificationRequestsException extends GcBoardBusinessException {
    public TooManyEmailVerificationRequestsException(Duration window, long maxRequests) {
        super(
                "이메일 인증 메일은 %d분 동안 최대 %d회까지만 요청할 수 있습니다. 잠시 후 다시 시도해 주세요."
                        .formatted(window.toMinutes(), maxRequests),
                HttpStatus.TOO_MANY_REQUESTS
        );
    }
}
