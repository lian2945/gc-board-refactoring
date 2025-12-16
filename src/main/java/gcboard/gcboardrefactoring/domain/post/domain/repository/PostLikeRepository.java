package gcboard.gcboardrefactoring.domain.post.domain.repository;

import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    Optional<PostLikeEntity> findByPostAndUserNickname(PostEntity postEntity, String userNickname);
}
