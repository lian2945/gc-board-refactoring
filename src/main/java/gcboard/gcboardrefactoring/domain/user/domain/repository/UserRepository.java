package gcboard.gcboardrefactoring.domain.user.domain.repository;

import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserQuerydslRepository {

    Optional<UserEntity> findByNickname(String nickname);
}