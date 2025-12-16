package gcboard.gcboardrefactoring.domain.auth.application.service;

import gcboard.gcboardrefactoring.domain.auth.application.event.EmailVerificationRequestedEvent;
import gcboard.gcboardrefactoring.domain.auth.application.event.EmailVerifiedEvent;
import gcboard.gcboardrefactoring.domain.auth.exception.*;
import gcboard.gcboardrefactoring.domain.auth.presentation.dto.request.LoginRequestDto;
import gcboard.gcboardrefactoring.domain.auth.presentation.dto.request.SendEmailVerificationRequestDto;
import gcboard.gcboardrefactoring.domain.auth.presentation.dto.request.SignupRequestDto;
import gcboard.gcboardrefactoring.domain.auth.presentation.dto.response.AuthTokensResponseDto;
import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserRepository;
import gcboard.gcboardrefactoring.domain.user.exception.AlreadyExsitsNicknameException;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.UserLoginQueryDto;
import gcboard.gcboardrefactoring.global.constants.JwtConstants;
import gcboard.gcboardrefactoring.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String mail = emailVerificationService.consumeMailByCode(signupRequestDto.mailVerificationCode());
        if (userRepository.existsByNickname(signupRequestDto.nickname())) {
            throw new AlreadyExsitsNicknameException();
        }

        if (userRepository.existsByMail(mail)) {
            throw new AlreadyExistsMailException();
        }

        UserEntity userEntity = UserEntity.builder()
                .mail(mail)
                .nickname(signupRequestDto.nickname())
                .password(passwordEncoder.encode(signupRequestDto.password()))
                .profile(signupRequestDto.profile())
                .description(signupRequestDto.description())
                .build();

        userRepository.save(userEntity);
        eventPublisher.publishEvent(new EmailVerifiedEvent(userEntity.getMail(), userEntity.getNickname()));
    }

    @Transactional(readOnly = true)
    public void sendVerificationEmail(SendEmailVerificationRequestDto requestDto) {
        if (userRepository.existsByMail(requestDto.mail())) {
            throw new AlreadyExistsMailException();
        }

        publishVerification(requestDto.mail(), requestDto.nickname());
    }

    @Transactional(readOnly = true)
    public AuthTokensResponseDto login(LoginRequestDto loginRequestDto) {
        UserLoginQueryDto loginUser = userRepository.findLoginUserByNickname(loginRequestDto.nickname())
                .orElseThrow(InvalidCredentialsException::new);

        if (!loginUser.isActive()) {
            throw new InactiveUserException();
        }

        if (!passwordEncoder.matches(loginRequestDto.password(), loginUser.password())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(loginUser.nickname());
        String refreshToken = jwtTokenProvider.createRefreshToken(loginUser.nickname());

        return new AuthTokensResponseDto(
                accessToken,
                refreshToken,
                JwtConstants.AUTHORIZATION_HEADER_PREFIX.trim(),
                jwtTokenProvider.getAccessTokenTtlSeconds(),
                jwtTokenProvider.getRefreshTokenTtlSeconds()
        );
    }

    private void publishVerification(String mail, String nickname) {
        String code = emailVerificationService.issueCode(mail);
        long expiresInMinutes = emailVerificationService.getTtl().toMinutes();
        String displayName = (nickname != null && !nickname.isBlank()) ? nickname : mail;
        eventPublisher.publishEvent(new EmailVerificationRequestedEvent(mail, displayName, code, expiresInMinutes));
    }
}
