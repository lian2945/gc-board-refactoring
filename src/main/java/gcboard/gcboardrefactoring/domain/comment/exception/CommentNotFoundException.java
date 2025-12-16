package gcboard.gcboardrefactoring.domain.comment.exception;

import gcboard.gcboardrefactoring.global.entity.exception.ResourceNotFoundException;

public class CommentNotFoundException extends ResourceNotFoundException {
    public CommentNotFoundException() {
        super("댓글을 찾을 수 없습니다.");
    }
}
