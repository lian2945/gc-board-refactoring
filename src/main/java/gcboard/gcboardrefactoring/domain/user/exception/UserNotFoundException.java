package gcboard.gcboardrefactoring.domain.user.exception;

import gcboard.gcboardrefactoring.global.entity.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public static final UserNotFoundException EXCEPTION = new UserNotFoundException();
    
    public UserNotFoundException() {
        super("요청한 교사를 찾을 수 없습니다.");
    }
}
