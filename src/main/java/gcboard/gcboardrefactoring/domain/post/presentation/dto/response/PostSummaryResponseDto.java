package gcboard.gcboardrefactoring.domain.post.presentation.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PostSummaryResponseDto(
        @JsonProperty("postId") String postId,
        String title,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long likeCount,
        Long liked
) {
    // Native query용 생성자 (String을 직접 받음)
    public PostSummaryResponseDto {
    }

    public boolean isLiked() {
        return liked != null && liked == 1L;
    }
}
