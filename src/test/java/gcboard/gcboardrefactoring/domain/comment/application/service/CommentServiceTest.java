package gcboard.gcboardrefactoring.domain.comment.application.service;

import gcboard.gcboardrefactoring.domain.comment.application.dto.request.CreateCommentRequestDto;
import gcboard.gcboardrefactoring.domain.comment.domain.entity.CommentEntity;
import gcboard.gcboardrefactoring.domain.comment.domain.repository.CommentRepository;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentNotFoundException;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.domain.repository.PostRepository;
import gcboard.gcboardrefactoring.domain.post.exception.PostNotFoundException;
import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserRepository;
import gcboard.gcboardrefactoring.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // given
        Long postId = 1L;
        String nickname = "testuser";
        CreateCommentRequestDto request = new CreateCommentRequestDto("Test Comment");

        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .email("test@example.com")
                .password("password")
                .build();

        PostEntity post = PostEntity.builder()
                .id(postId)
                .title("Test Post")
                .content("Content")
                .authorNickname("author")
                .build();

        CommentEntity savedComment = CommentEntity.builder()
                .id(100L)
                .postId(postId)
                .content(request.content())
                .authorNickname(nickname)
                .depth(0)
                .path("path")
                .build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(commentRepository.save(any(CommentEntity.class))).willReturn(savedComment);

        // when
        var result = commentService.createComment(postId, request, nickname, null);

        // then
        assertThat(result.content()).isEqualTo("Test Comment");
        verify(commentRepository, times(1)).save(any(CommentEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 작성 시 실패")
    void createComment_PostNotFound_ThrowsException() {
        // given
        Long postId = 999L;
        String nickname = "testuser";
        CreateCommentRequestDto request = new CreateCommentRequestDto("Comment");

        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .email("test@example.com")
                .password("password")
                .build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, request, nickname, null))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 댓글 작성 시 실패")
    void createComment_UserNotFound_ThrowsException() {
        // given
        Long postId = 1L;
        String nickname = "nonexistent";
        CreateCommentRequestDto request = new CreateCommentRequestDto("Comment");

        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, request, nickname, null))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시 실패")
    void deleteComment_NotFound_ThrowsException() {
        // given
        Long commentId = 999L;
        String nickname = "testuser";

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(commentId, nickname))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
