package gcboard.gcboardrefactoring.global.infrastructure.feign.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientBadRequestException extends GcBoardBusinessException {
    public FeignClientBadRequestException() {
        super("요청한 정보를 처리할 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
