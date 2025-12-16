package gcboard.gcboardrefactoring.domain.post.domain.entity;

import gcboard.gcboardrefactoring.domain.comment.domain.entity.CommentEntity;
import gcboard.gcboardrefactoring.global.entity.GcBoardEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(
        name = "post",
        indexes = {
                @Index(name = "idx_post_author", columnList = "author_nickname"),
                @Index(name = "idx_post_created_at", columnList = "created_at"),
                @Index(name = "idx_post_content_fulltext", columnList = "content", unique = false)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity extends GcBoardEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_nickname", nullable = false)
    private String authorNickname;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "accepted_comment_id")
    private Long acceptedCommentId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLikeEntity> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    @Builder
    public PostEntity(String title, String content, String authorNickname) {
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.likeCount = 0L;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void acceptComment(Long commentId) {
        this.acceptedCommentId = commentId;
    }

    public void unacceptComment() {
        this.acceptedCommentId = null;
    }

    public void increaseLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount = this.likeCount - 1;
        }
    }
}
