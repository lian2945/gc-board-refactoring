package gcboard.gcboardrefactoring.global.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {
    private Map<String, OAuth2ProviderProperties> providers;

    public record OAuth2ProviderProperties(
        String loginUrl,
        String tokenUrl,
        String userInfoUrl,
        String clientId,
        String redirectUrl,
        String clientSecret,
        String grantType
    ) {}
}
