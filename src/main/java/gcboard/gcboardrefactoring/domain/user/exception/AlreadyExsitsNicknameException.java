package gcboard.gcboardrefactoring.domain.user.exception;

import gcboard.gcboardrefactoring.global.entity.exception.DuplicateException;

public class AlreadyExsitsNicknameException extends DuplicateException {
    public AlreadyExsitsNicknameException() {
        super("해당 닉네임은 이미 존재합니다.");
    }
}
