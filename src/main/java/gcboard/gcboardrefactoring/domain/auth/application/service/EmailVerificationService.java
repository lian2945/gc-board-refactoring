package gcboard.gcboardrefactoring.domain.auth.application.service;

import gcboard.gcboardrefactoring.domain.auth.exception.InvalidVerificationCodeException;
import gcboard.gcboardrefactoring.domain.auth.exception.TooManyEmailVerificationRequestsException;
import gcboard.gcboardrefactoring.domain.auth.exception.VerificationCodeExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final String KEY_MAIL_PREFIX = "email:verify:mail:";
    private static final String KEY_CODE_PREFIX = "email:verify:code:";
    private static final String KEY_REQUEST_COUNT_PREFIX = "email:verify:request-count:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(15);
    private static final Duration REQUEST_LIMIT_WINDOW = Duration.ofHours(1);
    private static final long MAX_REQUESTS_PER_WINDOW = 5;

    private final StringRedisTemplate stringRedisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public String issueCode(String mail) {
        validateRequestLimit(mail);

        String code = generateCode();
        stringRedisTemplate.opsForValue().set(mailKey(mail), code, DEFAULT_TTL);
        stringRedisTemplate.opsForValue().set(codeKey(code), mail, DEFAULT_TTL);
        return code;
    }

    public String consumeMailByCode(String code) {
        String mail = stringRedisTemplate.opsForValue().get(codeKey(code));
        if (mail == null) {
            throw new VerificationCodeExpiredException();
        }

        String cachedCode = stringRedisTemplate.opsForValue().get(mailKey(mail));
        if (cachedCode == null) {
            throw new VerificationCodeExpiredException();
        }
        if (!cachedCode.equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        deleteKeys(mail, code);
        return mail;
    }

    public Duration getTtl() {
        return DEFAULT_TTL;
    }

    private void deleteKeys(String mail, String code) {
        stringRedisTemplate.delete(mailKey(mail));
        stringRedisTemplate.delete(codeKey(code));
    }

    private void validateRequestLimit(String mail) {
        String key = requestCountKey(mail);
        Long requestCount = stringRedisTemplate.opsForValue().increment(key);
        if (requestCount == null) {
            throw new IllegalStateException("인증 메일 요청 횟수를 확인할 수 없습니다.");
        }

        if (requestCount == 1L) {
            stringRedisTemplate.expire(key, REQUEST_LIMIT_WINDOW);
        }
        if (requestCount > MAX_REQUESTS_PER_WINDOW) {
            throw new TooManyEmailVerificationRequestsException(REQUEST_LIMIT_WINDOW, MAX_REQUESTS_PER_WINDOW);
        }
    }

    private String mailKey(String mail) {
        return KEY_MAIL_PREFIX + mail;
    }

    private String codeKey(String code) {
        return KEY_CODE_PREFIX + code;
    }

    private String requestCountKey(String mail) {
        return KEY_REQUEST_COUNT_PREFIX + mail;
    }

    private String generateCode() {
        int code = secureRandom.nextInt(900000) + 100000; // 6 digits
        return Integer.toString(code);
    }
}
