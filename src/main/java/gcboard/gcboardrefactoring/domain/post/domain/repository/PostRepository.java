package gcboard.gcboardrefactoring.domain.post.domain.repository;

import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostSummaryResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query(
            value = """
                    SELECT
                      CAST(p.id AS CHAR) AS postId,
                      p.title AS title,
                      p.author_nickname AS authorNickname,
                      p.created_at AS createdAt,
                      p.updated_at AS updatedAt,
                      p.like_count AS likeCount,
                      IF(:nickname IS NOT NULL AND EXISTS (
                           SELECT 1
                           FROM post_like pl
                           WHERE pl.post_id = p.id
                             AND pl.user_nickname = :nickname
                      ), TRUE, FALSE) AS liked
                    FROM post p
                    WHERE p.created_at < :lastReadAt
                    ORDER BY p.created_at DESC, p.id DESC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<PostSummaryResponseDto> findByCreatedAtBefore(
            @Param("lastReadAt") LocalDateTime lastReadAt,
            @Param("nickname") String nickname,
            @Param("limit") long limit
    );

    @Query(
            value = """
                    SELECT CAST(p.id AS CHAR)      AS id,
                           p.title   AS title,
                           p.author_nickname AS authorNickname,
                           p.created_at AS createdAt,
                           p.updated_at AS updatedAt,
                           p.like_count AS likeCount,
                           CASE WHEN :nickname IS NULL THEN FALSE
                                WHEN EXISTS (
                                     SELECT 1
                                     FROM post_like pl
                                     WHERE pl.post_id = p.id
                                       AND pl.user_nickname = :nickname
                                ) THEN TRUE
                                ELSE FALSE
                           END AS liked
                    FROM post p
                    WHERE p.created_at < :lastReadAt
                      AND p.title LIKE CONCAT('%', :keyword, '%')
                    ORDER BY p.created_at DESC, p.id DESC
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<PostSummaryResponseDto> searchByFullText(
            @Param("keyword") String keyword,
            @Param("lastReadAt") LocalDateTime lastReadAt,
            @Param("nickname") String nickname,
            @Param("limit") long limit
    );
}
