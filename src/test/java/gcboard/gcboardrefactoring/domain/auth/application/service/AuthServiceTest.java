package gcboard.gcboardrefactoring.domain.auth.application.service;

import gcboard.gcboardrefactoring.domain.auth.application.dto.request.SignUpRequestDto;
import gcboard.gcboardrefactoring.domain.auth.domain.entity.EmailVerification;
import gcboard.gcboardrefactoring.domain.auth.domain.repository.EmailVerificationRepository;
import gcboard.gcboardrefactoring.domain.auth.exception.EmailNotVerifiedException;
import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("이메일 인증이 완료되지 않으면 회원가입 실패")
    void signUp_NotVerified_ThrowsException() {
        // given
        SignUpRequestDto request = new SignUpRequestDto(
                "test@example.com",
                "password123",
                "testuser",
                "123456"
        );

        given(emailVerificationRepository.findByEmailAndIsVerifiedTrue(anyString()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(EmailNotVerifiedException.class);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("이메일 인증이 완료되면 회원가입 성공")
    void signUp_Verified_Success() {
        // given
        SignUpRequestDto request = new SignUpRequestDto(
                "test@example.com",
                "password123",
                "testuser",
                "123456"
        );

        EmailVerification verification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("123456")
                .isVerified(true)
                .build();

        given(emailVerificationRepository.findByEmailAndIsVerifiedTrue(anyString()))
                .willReturn(Optional.of(verification));
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(UserEntity.class))).willAnswer(i -> i.getArgument(0));

        // when
        authService.signUp(request);

        // then
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
