package gcboard.gcboardrefactoring.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "profanity")
public class ProfanityProperties {
    private final List<String> forbiddenWords = new ArrayList<>();
}
