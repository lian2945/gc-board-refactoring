package gcboard.gcboardrefactoring.global.configuration;

import gcboard.gcboardrefactoring.global.properties.MailProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        sender.setProtocol("smtp");
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties props = new Properties();
        props.putAll(mailProperties.getProperties());
        props.putIfAbsent("mail.smtp.starttls.enable", "true");
        props.putIfAbsent("mail.smtp.starttls.required", "true");
        props.putIfAbsent("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", mailProperties.getHost());
        props.put("mail.smtp.ssl.protocols", "TLSv1.3");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.putIfAbsent("mail.smtp.connectiontimeout", "15000");
        props.putIfAbsent("mail.smtp.timeout", "15000");
        props.putIfAbsent("mail.smtp.writetimeout", "15000");
        sender.setJavaMailProperties(props);
        return sender;
    }
}
