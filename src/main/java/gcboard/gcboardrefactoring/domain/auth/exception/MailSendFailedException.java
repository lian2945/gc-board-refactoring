package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.exception.GcBoardSystemError;
import org.springframework.http.HttpStatus;

public class MailSendFailedException extends GcBoardSystemError {
    public MailSendFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
