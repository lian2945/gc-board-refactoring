package gcboard.gcboardrefactoring.global.infrastructure.feign.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientUnauthorizedException extends GcBoardBusinessException {
    public FeignClientUnauthorizedException() {
        super("외부 서비스 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);
    }
}
