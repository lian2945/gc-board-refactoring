package gcboard.gcboardrefactoring.domain.auth.presentation.dto.response;

import jakarta.validation.constraints.NotNull;

public record AuthTokensResponseDto(
        @NotNull String accessToken,
        @NotNull String refreshToken,
        @NotNull String tokenType,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {
}
