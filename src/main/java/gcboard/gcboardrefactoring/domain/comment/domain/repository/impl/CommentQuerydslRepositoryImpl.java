package gcboard.gcboardrefactoring.domain.comment.domain.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gcboard.gcboardrefactoring.domain.comment.domain.repository.CommentQuerydslRepository;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentTreeResponseDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.QCommentTreeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gcboard.gcboardrefactoring.domain.comment.domain.entity.QCommentEntity.commentEntity;

@Repository
@RequiredArgsConstructor
public class CommentQuerydslRepositoryImpl implements CommentQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentTreeResponseDto> findByPostIdWithCursor(Long postId, String parentPathPrefix, String cursorPath, long limit) {
        return jpaQueryFactory
                .select(new QCommentTreeResponseDto(
                        commentEntity.id,
                        commentEntity.post.id,
                        commentEntity.parent.id,
                        commentEntity.rootId,
                        commentEntity.depth,
                        commentEntity.content,
                        commentEntity.authorNickname,
                        commentEntity.isDeleted,
                        commentEntity.path,
                        commentEntity.createdAt,
                        commentEntity.updatedAt
                ))
                .from(commentEntity)
                .where(
                        commentEntity.post.id.eq(postId),
                        prefixCondition(parentPathPrefix),
                        pathAfterCursor(cursorPath)
                )
                .orderBy(commentEntity.path.asc(), commentEntity.id.asc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression pathAfterCursor(String cursorPath) {
        if (cursorPath == null || cursorPath.isBlank()) {
            return null;
        }
        return commentEntity.path.gt(cursorPath);
    }

    private BooleanExpression prefixCondition(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return null;
        }
        return commentEntity.path.startsWith(prefix);
    }
}
