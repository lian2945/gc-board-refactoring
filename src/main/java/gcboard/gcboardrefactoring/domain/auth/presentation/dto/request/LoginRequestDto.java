package gcboard.gcboardrefactoring.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank String nickname,
        @NotBlank String password
) {
}
