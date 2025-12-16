package gcboard.gcboardrefactoring.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequestDto(
        @NotBlank @Size(min = 6, max = 6) @Pattern(regexp = "\\d{6}") String code
) {
}
