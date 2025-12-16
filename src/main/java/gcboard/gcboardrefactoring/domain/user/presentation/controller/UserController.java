package gcboard.gcboardrefactoring.domain.user.presentation.controller;

import gcboard.gcboardrefactoring.domain.user.application.service.UserService;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.request.UserProfileUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.user.presentation.dto.response.UserProfileResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{nickname}")
    public UserProfileResponseDto getUserProfile(
            @NotNull
            @Validated // MethodValidationPostProcessor Proxy 객체 검증
            @PathVariable("nickname") String nickname
    ) {
        return userService.getUserProfile(nickname);
    }

    @GetMapping("/profile")
    public UserProfileResponseDto getUserProfile(@AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails) {
        return userService.getUserProfile(gcBoardUserDetails.getUsername());
    }

    @PatchMapping("/profile")
    public void updateUserProfile(
            @AuthenticationPrincipal GcBoardUserDetails gcBoardUserDetails,
            @Validated @RequestBody UserProfileUpdateRequestDto userProfileUpdateRequestDto
    ) {
        userService.updateUserProfile(gcBoardUserDetails, userProfileUpdateRequestDto);
    }
}
