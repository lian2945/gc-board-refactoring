package gcboard.gcboardrefactoring.global.infrastructure.feign.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientTimeoutException extends GcBoardBusinessException {
    public FeignClientTimeoutException() {
        super("외부 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT);
    }
}
