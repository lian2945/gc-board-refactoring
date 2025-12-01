package gcboard.gcboardrefactoring.global.infrastructure.feign.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientServiceUnavailableException extends GcBoardBusinessException {
    public FeignClientServiceUnavailableException() {
        super("외부 서비스를 현재 사용할 수 없습니다.", HttpStatus.BAD_GATEWAY);
    }
}
