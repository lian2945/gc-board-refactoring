package gcboard.gcboardrefactoring.domain.post.exception;

import gcboard.gcboardrefactoring.global.entity.exception.ResourceNotFoundException;

public class PostNotFoundException extends ResourceNotFoundException {
    public PostNotFoundException() {
        super("게시글을 찾을 수 없습니다.");
    }
}
