package gcboard.gcboardrefactoring.domain.user.domain.entity;

import gcboard.gcboardrefactoring.domain.user.domain.enums.Role;
import gcboard.gcboardrefactoring.global.entity.GcBoardEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "teacher")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends GcBoardEntity {
    @Column(name = "mail", nullable = false, updatable = false)
    private String mail;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile")
    private String profile;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public UserEntity(String mail, String nickname, String password, String profile, String description) {
        this.mail = mail;
        this.nickname = nickname;
        this.password = password;
        this.profile = profile;
        this.description = description;
        this.role = Role.USER;
        this.isActive = true;
    }

    void changeRole(Role role) {
        this.role = role;
    }

    public void updateProfile(String profile, String description) {
        this.profile = profile;
        this.description = description;
    }
}
