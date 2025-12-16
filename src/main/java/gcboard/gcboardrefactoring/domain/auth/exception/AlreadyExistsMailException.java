package gcboard.gcboardrefactoring.domain.auth.exception;

import gcboard.gcboardrefactoring.global.entity.exception.DuplicateException;

public class AlreadyExistsMailException extends DuplicateException {
    public AlreadyExistsMailException() {
        super("해당 이메일은 이미 가입되었습니다.");
    }
}
