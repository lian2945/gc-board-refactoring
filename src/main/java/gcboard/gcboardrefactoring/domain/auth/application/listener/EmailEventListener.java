package gcboard.gcboardrefactoring.domain.auth.application.listener;

import gcboard.gcboardrefactoring.domain.auth.application.event.EmailVerificationRequestedEvent;
import gcboard.gcboardrefactoring.domain.auth.application.event.EmailVerifiedEvent;
import gcboard.gcboardrefactoring.domain.auth.exception.MailSendFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailEventListener {

    private final JavaMailSender mailSender;

    public EmailEventListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @EventListener
    public void handleEmailVerificationRequestedEvent(EmailVerificationRequestedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.mail());
            message.setSubject("GC Board 이메일 인증 코드");
            message.setText(buildVerificationBody(event));
            mailSender.send(message);
        } catch (MailException e) {
            throw new MailSendFailedException(e.getMessage());
        }
    }

    @EventListener
    public void handleEmailVerifiedEvent(EmailVerifiedEvent event) {
        log.info("이메일 인증 완료: {}", event.mail());
    }

    private String buildVerificationBody(EmailVerificationRequestedEvent event) {
        return """
                안녕하세요, %s님!

                아래 인증 코드를 입력해 이메일을 인증해 주세요.

                인증 코드: %s
                만료 시간: %d분

                감사합니다.
                """.formatted(event.nickname(), event.code(), event.expiresInMinutes());
    }
}
