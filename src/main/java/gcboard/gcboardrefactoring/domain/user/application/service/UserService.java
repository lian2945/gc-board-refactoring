package gcboard.gcboardrefactoring.domain.user.application.service;

import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import gcboard.gcboardrefactoring.domain.user.domain.repository.UserRepository;
import gcboard.gcboardrefactoring.domain.user.exception.UserNotFoundException;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.request.UserProfileUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.UserProfileResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponseDto getUserProfile(String nickname) {
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        return UserProfileResponseDto.builder()
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .description(user.getDescription())
                .build();
    }

    @Transactional
    public void updateUserProfile(GcBoardUserDetails userDetails, UserProfileUpdateRequestDto dto) {
        UserEntity user = userRepository.findByNickname(userDetails.getUsername())
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        
        user.updateProfile(dto.getProfile(), dto.getDescription());
    }
}
