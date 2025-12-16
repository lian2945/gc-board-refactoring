package gcboard.gcboardrefactoring.domain.post.presentation;

import gcboard.gcboardrefactoring.domain.post.application.service.PostService;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostCreateRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostSearchRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.request.PostUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostLikeResponseDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostResponseDto;
import gcboard.gcboardrefactoring.domain.post.presentation.dto.response.PostSummaryResponseDto;
import gcboard.gcboardrefactoring.global.cursor.request.LastReadCursorRequestDto;
import gcboard.gcboardrefactoring.global.cursor.response.CursorResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponseDto createPost(
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails,
            @Validated @RequestBody PostCreateRequestDto requestDto
    ) {
        return postService.createPost(gcBoardUserDetails, requestDto);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(
            @NotNull @PathVariable("postId") Long postId,
            Authentication authentication
    ) {
        return postService.getPost(postId, resolveUser(authentication));
    }

    @GetMapping
    public CursorResponseDto<PostSummaryResponseDto> getPosts(
            Authentication authentication,
            @RequestParam(value = "lastReadAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime lastReadAt,
            @RequestParam(value = "count", defaultValue = "10")
            @Min(value = 1, message = "count(조회 개수)는 1 이상입니다.")
            @Max(value = 50, message = "count(조회 개수)는 50 이하입니다.")
                    Long count
    ) {
        LastReadCursorRequestDto cursorRequestDto = new LastReadCursorRequestDto(lastReadAt, count);
        return postService.getPosts(cursorRequestDto, resolveUser(authentication));
    }

    @GetMapping("/search")
    public CursorResponseDto<PostSummaryResponseDto> searchPosts(
            Authentication authentication,
            @RequestParam("keyword")
            @NotBlank(message = "keyword(검색어)는 필수입니다.")
                    String keyword,
            @RequestParam(value = "lastReadAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime lastReadAt,
            @RequestParam(value = "count", defaultValue = "20")
            @Min(value = 1, message = "count(조회 개수)는 1 이상입니다.")
            @Max(value = 50, message = "count(조회 개수)는 50 이하입니다.")
                    Long count
    ) {
        LastReadCursorRequestDto cursor = new LastReadCursorRequestDto(lastReadAt, count);
        PostSearchRequestDto searchRequestDto = new PostSearchRequestDto(keyword, cursor);
        return postService.searchPosts(searchRequestDto, resolveUser(authentication));
    }

    @PatchMapping("/{postId}")
    public PostResponseDto updatePost(
            @NotNull @PathVariable("postId") Long postId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails,
            @Validated @RequestBody PostUpdateRequestDto requestDto
    ) {
        return postService.updatePost(postId, gcBoardUserDetails, requestDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            @NotNull @PathVariable("postId") Long postId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails
    ) {
        postService.deletePost(postId, gcBoardUserDetails);
    }

    @PostMapping("/{postId}/likes")
    public PostLikeResponseDto toggleLike(
            @NotNull @PathVariable("postId") Long postId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails
    ) {
        return postService.toggleLike(postId, gcBoardUserDetails);
    }

    private GcBoardUserDetails resolveUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof GcBoardUserDetails details) {
            return details;
        }
        return null;
    }
}
