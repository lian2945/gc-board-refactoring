package gcboard.gcboardrefactoring.domain.post.application.service;

import gcboard.gcboardrefactoring.domain.post.application.dto.request.CreatePostRequestDto;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.domain.repository.PostLikeRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        // given
        String nickname = "testuser";
        CreatePostRequestDto request = new CreatePostRequestDto("Test Title", "Test Content");

        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .email("test@example.com")
                .password("password")
                .build();

        PostEntity savedPost = PostEntity.builder()
                .id(1L)
                .title(request.title())
                .content(request.content())
                .authorNickname(nickname)
                .build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(postRepository.save(any(PostEntity.class))).willReturn(savedPost);

        // when
        var result = postService.createPost(request, nickname);

        // then
        assertThat(result.title()).isEqualTo("Test Title");
        assertThat(result.content()).isEqualTo("Test Content");
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 게시글 생성 시 실패")
    void createPost_UserNotFound_ThrowsException() {
        // given
        String nickname = "nonexistent";
        CreatePostRequestDto request = new CreatePostRequestDto("Test", "Content");

        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.createPost(request, nickname))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 실패")
    void getPost_NotFound_ThrowsException() {
        // given
        Long postId = 999L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPost(postId, null))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("게시글 좋아요 토글 성공")
    void toggleLike_Success() {
        // given
        Long postId = 1L;
        String nickname = "testuser";

        PostEntity post = PostEntity.builder()
                .id(postId)
                .title("Test")
                .content("Content")
                .authorNickname("author")
                .likeCount(0L)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserNickname(postId, nickname)).willReturn(false);

        // when
        postService.toggleLike(postId, nickname);

        // then
        verify(postLikeRepository, times(1)).save(any());
        assertThat(post.getLikeCount()).isEqualTo(1L);
    }
}
