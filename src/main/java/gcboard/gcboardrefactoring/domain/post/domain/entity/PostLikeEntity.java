package gcboard.gcboardrefactoring.domain.post.domain.entity;

import gcboard.gcboardrefactoring.global.entity.GcBoardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_like_post_user", columnNames = {"post_id", "user_nickname"})
        },
        indexes = {
                @Index(name = "idx_post_like_post", columnList = "post_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeEntity extends GcBoardEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(name = "user_nickname", nullable = false)
    private String userNickname;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Builder
    public PostLikeEntity(PostEntity post, String userNickname) {
        this.post = post;
        this.userNickname = userNickname;
    }
}
