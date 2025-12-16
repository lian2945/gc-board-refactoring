package gcboard.gcboardrefactoring.global.configuration;

import gcboard.gcboardrefactoring.domain.user.domain.enums.Role;
import gcboard.gcboardrefactoring.global.security.filter.GcBoardAuthenticationExceptionFilter;
import gcboard.gcboardrefactoring.global.security.filter.GcBoardAuthenticationFilter;
import gcboard.gcboardrefactoring.global.security.jwt.JwtValidator;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JsonMapper jsonMapper;
    private final JwtValidator jwtValidator;
    private final GcBoardUserDetailsService gcBoardUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/reissue").permitAll()
                        .requestMatchers("/login/oauth2").permitAll()
                        .requestMatchers("/api/healthcheck").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .anonymous(anonymous -> anonymous
                        .principal(Role.GUEST.name())
                        .authorities(Role.GUEST.getValue())
                )
                .addFilterBefore(new GcBoardAuthenticationFilter(jwtValidator, gcBoardUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new GcBoardAuthenticationExceptionFilter(jsonMapper), GcBoardAuthenticationFilter.class);

        return http.build();
    }
}
