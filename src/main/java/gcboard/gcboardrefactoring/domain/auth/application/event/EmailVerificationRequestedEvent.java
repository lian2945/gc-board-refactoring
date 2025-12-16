package gcboard.gcboardrefactoring.domain.auth.application.event;

public record EmailVerificationRequestedEvent(
        String mail,
        String nickname,
        String code,
        long expiresInMinutes
) {
}
