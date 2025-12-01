package gcboard.gcboardrefactoring.domain.user.domain.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserQuerydslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQuerydslRepositoryImpl implements UserQuerydslRepository {
    private final JPAQueryFactory jpaQueryFactory;

}
