package gcboard.gcboardrefactoring.global.validation;

import gcboard.gcboardrefactoring.global.properties.ProfanityProperties;
import gcboard.gcboardrefactoring.global.exception.ForbiddenWordException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfanityValidator {

    private final Set<String> forbiddenWords;

    public ProfanityValidator(ProfanityProperties profanityProperties) {
        this.forbiddenWords = buildForbiddenWords(profanityProperties);
    }

    public void validateClean(String... texts) {
        if (texts == null) {
            return;
        }
        for (String text : texts) {
            if (text == null) {
                continue;
            }
            String normalized = text.toLowerCase(Locale.ROOT);
            if (containsForbidden(normalized)) {
                throw new ForbiddenWordException();
            }
        }
    }

    private boolean containsForbidden(String text) {
        return forbiddenWords.stream().anyMatch(text::contains);
    }

    private Set<String> buildForbiddenWords(ProfanityProperties profanityProperties) {
        Set<String> fromEnv = profanityProperties.getForbiddenWords().stream()
                .map(w -> w == null ? "" : w.trim().toLowerCase(Locale.ROOT))
                .filter(w -> !w.isBlank())
                .collect(Collectors.toSet());
        if (!fromEnv.isEmpty()) {
            return Collections.unmodifiableSet(fromEnv);
        }
        // 기본값: env 미설정 시 최소 금칙어
        return Set.of("fuck", "shit", "bitch", "씨발", "병신");
    }
}
