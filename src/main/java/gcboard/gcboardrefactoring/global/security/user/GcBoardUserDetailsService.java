package gcboard.gcboardrefactoring.global.security.user;

import gcboard.gcboardrefactoring.domain.user.domain.repository.UserRepository;
import gcboard.gcboardrefactoring.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GcBoardUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public GcBoardUserDetails loadUserByUsername(String nickname) throws UserNotFoundException {
        return userRepository.findByNickname(nickname)
                .map(GcBoardUserDetails::new)
                .orElseThrow(UserNotFoundException::new);
    }
}
