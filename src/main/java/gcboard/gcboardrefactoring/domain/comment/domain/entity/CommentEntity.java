package gcboard.gcboardrefactoring.domain.comment.domain.entity;

import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.global.entity.GcBoardEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "comment",
        indexes = {
                @Index(name = "idx_comment_post_path", columnList = "post_id, path"),
                @Index(name = "idx_comment_post_root", columnList = "post_id, root_id"),
                @Index(name = "idx_comment_parent", columnList = "parent_id"),
                @Index(name = "idx_comment_author", columnList = "author_nickname")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends GcBoardEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @Column(name = "root_id", nullable = false)
    private Long rootId;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Column(name = "path", nullable = false, length = 512)
    private String path;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_nickname", nullable = false)
    private String authorNickname;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Builder
    public CommentEntity(PostEntity post, CommentEntity parent, Long rootId, Integer depth, String content, String authorNickname) {
        this.post = post;
        this.parent = parent;
        this.rootId = rootId != null ? rootId : 0L;
        this.depth = depth != null ? depth : 0;
        this.path = "";
        this.content = content;
        this.authorNickname = authorNickname;
        this.isDeleted = false;
    }

    public void updateRootAndPath(Long rootId, String path) {
        this.rootId = rootId;
        this.path = path;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.content = "[삭제된 댓글]";
    }
}
