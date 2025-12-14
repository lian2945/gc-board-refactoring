package gcboard.gcboardrefactoring.global.cursor.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CursorRequestDto(
        @NotNull(message = "cursor(커서)는 필수 항목입니다.")
        Long cursor,

        @NotNull(message = "size(크기)는 필수 항목입니다.")
        @Min(value = 10, message = "size(크기)는 10 이상입니다.")
        @Max(value = 50, message = "size(크기)는 50 이하입니다.")
        Long size
) {
}
