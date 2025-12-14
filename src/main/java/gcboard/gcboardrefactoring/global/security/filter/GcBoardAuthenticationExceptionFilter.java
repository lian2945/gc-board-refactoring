package gcboard.gcboardrefactoring.global.security.filter;

import gcboard.gcboardrefactoring.domain.user.exception.UserNotFoundException;
import gcboard.gcboardrefactoring.global.constants.HttpResponseConstants;
import gcboard.gcboardrefactoring.global.exception.ErrorResponse;
import gcboard.gcboardrefactoring.global.security.exception.InvalidJsonWebTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GcBoardAuthenticationExceptionFilter extends OncePerRequestFilter {
    private final JsonMapper jsonMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch(UserNotFoundException e) {
            handleAuthException(response, HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(InvalidJsonWebTokenException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch(AuthenticationException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        catch(AccessDeniedException e) {
            handleAuthException(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
        catch(ExpiredJwtException e) {
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        }
        catch (JwtException e) {
            log.warn("JwtException 발생 : {}", e.getMessage());
            handleAuthException(response, HttpStatus.UNAUTHORIZED, "인증과정 중 오류가 발생했습니다.");
        }
    }

    private void handleAuthException(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        SecurityContextHolder.clearContext();

        response.setStatus(httpStatus.value());
        response.setContentType(HttpResponseConstants.CONTENT_TYPE);
        response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);

        ErrorResponse errorResponse = ErrorResponse.of(httpStatus.value(), message);

        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));
    }
}
