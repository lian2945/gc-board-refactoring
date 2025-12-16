package gcboard.gcboardrefactoring.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequestDto(
        @NotBlank @Size(min = 6, max = 6) @Pattern(regexp = "\\d{6}") String mailVerificationCode,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(min = 2, max = 32) String nickname,
        String profile,
        String description
) {
}
