package gcboard.gcboardrefactoring.global.security.user;

import gcboard.gcboardrefactoring.domain.user.domain.entity.UserEntity;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public record GcBoardUserDetails(@NonNull UserEntity userEntity) implements UserDetails {

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(userEntity.getRole().getValue()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @NonNull
    @Override
    public String getUsername() {
        return userEntity.getNickname();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getIsActive();
    }
}