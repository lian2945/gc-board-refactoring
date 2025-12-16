package gcboard.gcboardrefactoring.global.cursor.response;

import java.util.List;

public record CursorResponseDto<T>(
        List<T> content,
        int size,
        boolean last
) {
}
