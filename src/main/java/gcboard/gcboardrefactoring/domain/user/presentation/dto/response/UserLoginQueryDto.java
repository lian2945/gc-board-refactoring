package gcboard.gcboardrefactoring.domain.user.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import gcboard.gcboardrefactoring.domain.user.domain.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserLoginQueryDto(
        @NotNull Long id,
        @NotNull String mail,
        @NotNull String password,
        @NotNull String nickname,
        @NotNull Role role,
        @NotNull Boolean isActive
) {
    @QueryProjection
    public UserLoginQueryDto {
    }
}
