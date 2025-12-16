package gcboard.gcboardrefactoring.domain.comment.presentation;

import gcboard.gcboardrefactoring.domain.comment.application.service.CommentService;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentCreateRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentCursorRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentResponseDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentTreeResponseDto;
import gcboard.gcboardrefactoring.global.cursor.response.CursorResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto createComment(
            @NotNull @PathVariable("postId") Long postId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails,
            @RequestParam(value = "parent_comment_id", required = false) Long parentCommentId,
            @Validated @RequestBody CommentCreateRequestDto requestDto
    ) {
        return commentService.createComment(postId, gcBoardUserDetails, requestDto, parentCommentId);
    }

    @GetMapping
    public CursorResponseDto<CommentTreeResponseDto> getComments(
            @NotNull @PathVariable("postId") Long postId,
            @RequestParam(value = "parent_comment_id", required = false) Long parentCommentId,
            @RequestParam(value = "cursorPath", required = false) String cursorPath,
            @RequestParam(value = "count", defaultValue = "20") long count
    ) {
        CommentCursorRequestDto cursorRequestDto = new CommentCursorRequestDto(cursorPath, count);
        return commentService.getComments(postId, cursorRequestDto, parentCommentId);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(
            @NotNull @PathVariable("postId") Long postId,
            @NotNull @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails,
            @Validated @RequestBody CommentUpdateRequestDto requestDto
    ) {
        return commentService.updateComment(postId, commentId, gcBoardUserDetails, requestDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @NotNull @PathVariable("postId") Long postId,
            @NotNull @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails
    ) {
        commentService.deleteComment(postId, commentId, gcBoardUserDetails);
    }

    @PostMapping("/{commentId}/accept")
    public void acceptComment(
            @NotNull @PathVariable("postId") Long postId,
            @NotNull @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails
    ) {
        commentService.acceptComment(postId, commentId, gcBoardUserDetails);
    }

    @DeleteMapping("/{commentId}/accept")
    public void unacceptComment(
            @NotNull @PathVariable("postId") Long postId,
            @NotNull @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails
    ) {
        commentService.unacceptComment(postId, commentId, gcBoardUserDetails);
    }
}
