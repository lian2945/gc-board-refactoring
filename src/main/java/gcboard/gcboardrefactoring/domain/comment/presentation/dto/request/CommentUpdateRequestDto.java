package gcboard.gcboardrefactoring.domain.comment.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequestDto(
        @NotBlank(message = "content(내용)은 필수 항목입니다.")
        @Size(max = 2000, message = "content(내용)은 2000자 이하입니다.")
        String content
) {
}
