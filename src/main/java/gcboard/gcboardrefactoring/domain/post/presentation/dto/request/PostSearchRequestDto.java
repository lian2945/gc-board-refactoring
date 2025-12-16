package gcboard.gcboardrefactoring.domain.post.presentation.dto.request;

import gcboard.gcboardrefactoring.global.cursor.request.LastReadCursorRequestDto;
import jakarta.validation.constraints.NotBlank;

public record PostSearchRequestDto(
        @NotBlank(message = "keyword(검색어)는 필수입니다.")
        String keyword,
        LastReadCursorRequestDto cursor
) {
}
