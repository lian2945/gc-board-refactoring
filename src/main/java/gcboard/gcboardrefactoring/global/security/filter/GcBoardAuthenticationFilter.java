package gcboard.gcboardrefactoring.global.security.filter;

import gcboard.gcboardrefactoring.global.constants.JwtConstants;
import gcboard.gcboardrefactoring.global.security.jwt.JwtValidator;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class GcBoardAuthenticationFilter extends OncePerRequestFilter {
    private final JwtValidator jwtValidator;
    private final GcBoardUserDetailsService gcBoardUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if(jwtValidator.isInvalidAuthorizationHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String nickname = jwtValidator.getNicknameFromAuthorizationHeader(authHeader);
        GcBoardUserDetails teachmonUserDetails = gcBoardUserDetailsService.loadUserByUsername(nickname);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(teachmonUserDetails, null, teachmonUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
