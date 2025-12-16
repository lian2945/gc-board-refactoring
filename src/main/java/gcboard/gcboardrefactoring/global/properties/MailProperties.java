package gcboard.gcboardrefactoring.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
    /**
     * 기본값을 지정해 두어 설정이 비어 있어도 NPE 없이 동작하도록 한다.
     */
    private String host = "localhost";
    private Integer port = 1025;
    private String username;
    private String password;
    private final Map<String, String> properties = new HashMap<>();

    public MailProperties() {
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
