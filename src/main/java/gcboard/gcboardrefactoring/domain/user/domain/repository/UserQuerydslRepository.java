package gcboard.gcboardrefactoring.domain.user.domain.repository;

import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.UserLoginQueryDto;

import java.util.Optional;

public interface UserQuerydslRepository {
    Optional<UserLoginQueryDto> findLoginUserByNickname(String nickname);
}
