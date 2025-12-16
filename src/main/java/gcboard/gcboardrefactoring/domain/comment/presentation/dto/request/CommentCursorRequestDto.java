package gcboard.gcboardrefactoring.domain.comment.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCursorRequestDto(
        @Size(max = 1024, message = "cursorPath는 1024자 이하입니다.")
        String cursorPath,

        @NotNull(message = "count(조회 개수)는 필수 항목입니다.")
        @Min(value = 1, message = "count(조회 개수)는 1 이상입니다.")
        @Max(value = 50, message = "count(조회 개수)는 50 이하입니다.")
        Long count
) {
}
