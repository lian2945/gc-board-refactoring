package gcboard.gcboardrefactoring.domain.post.presentation.dto.response;

public record PostLikeResponseDto(
        Long postId,
        boolean liked,
        Long likeCount
) {
}
