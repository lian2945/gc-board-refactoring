package gcboard.gcboardrefactoring.global.security.jwt;

import gcboard.gcboardrefactoring.global.constants.JwtConstants;
import gcboard.gcboardrefactoring.global.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.accessExpirationMs = jwtProperties.getAccessExpiration();
        this.refreshExpirationMs = jwtProperties.getRefreshExpiration();
    }

    public String createAccessToken(String nickname) {
        return createToken(nickname, accessExpirationMs);
    }

    public String createRefreshToken(String nickname) {
        return createToken(nickname, refreshExpirationMs);
    }

    public long getAccessTokenTtlSeconds() {
        return accessExpirationMs / 1000;
    }

    public long getRefreshTokenTtlSeconds() {
        return refreshExpirationMs / 1000;
    }

    private String createToken(String nickname, long expirationMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(nickname)
                .claim(JwtConstants.CLAIM_MADY_BY_KEY, JwtConstants.CLAIM_MADY_BY_VALUE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(secretKey)
                .compact();
    }
}
