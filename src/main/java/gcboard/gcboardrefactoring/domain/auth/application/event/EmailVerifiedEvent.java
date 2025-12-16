package gcboard.gcboardrefactoring.domain.auth.application.event;

public record EmailVerifiedEvent(
        String mail,
        String nickname
) {
}
