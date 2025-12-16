package gcboard.gcboardrefactoring.domain.user.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {
    private String nickname;
    private String profile;
    private String description;

    @QueryProjection
    public UserProfileResponseDto(String nickname, String profile, String description) {
        this.nickname = nickname;
        this.profile = profile;
        this.description = description;
    }
}
