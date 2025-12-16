package gcboard.gcboardrefactoring.global.cursor.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record LastReadCursorRequestDto(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime lastReadAt,

        @NotNull(message = "count(조회 개수)는 필수 항목입니다.")
        @Min(value = 1, message = "count(조회 개수)는 1 이상입니다.")
        @Max(value = 50, message = "count(조회 개수)는 50 이하입니다.")
        Long count
) {
}
