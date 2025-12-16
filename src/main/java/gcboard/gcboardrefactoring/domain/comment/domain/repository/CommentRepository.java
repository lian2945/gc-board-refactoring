package gcboard.gcboardrefactoring.domain.comment.domain.repository;

import gcboard.gcboardrefactoring.domain.comment.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>, CommentQuerydslRepository {
}
