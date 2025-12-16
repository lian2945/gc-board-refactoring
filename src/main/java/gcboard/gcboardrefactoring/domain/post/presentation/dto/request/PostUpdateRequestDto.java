package gcboard.gcboardrefactoring.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateRequestDto(
        @NotBlank(message = "title(제목)은 필수 항목입니다.")
        @Size(max = 255, message = "title(제목)은 255자 이하입니다.")
        String title,

        @NotBlank(message = "content(내용)은 필수 항목입니다.")
        String content
) {
}
