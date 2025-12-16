package gcboard.gcboardrefactoring.domain.user.domain.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserQuerydslRepository;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.QUserLoginQueryDto;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.UserLoginQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static gcboard.gcboardrefactoring.domain.user.domain.entity.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class UserQuerydslRepositoryImpl implements UserQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<UserLoginQueryDto> findLoginUserByNickname(String nickname) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(new QUserLoginQueryDto(
                                userEntity.id,
                                userEntity.mail,
                                userEntity.password,
                                userEntity.nickname,
                                userEntity.role,
                                userEntity.isActive
                        ))
                        .from(userEntity)
                        .where(userEntity.nickname.eq(nickname))
                        .fetchOne()
        );
    }
}
