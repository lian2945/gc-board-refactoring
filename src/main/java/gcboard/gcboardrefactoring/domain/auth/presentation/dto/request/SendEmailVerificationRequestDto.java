package gcboard.gcboardrefactoring.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequestDto(
        @Email @NotBlank String mail,
        String nickname
) {
}
