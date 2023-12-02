package account.security;

import account.entity.SecurityEvent;
import account.repository.SecurityRepository;
import account.route.v1.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityRepository securityRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(customUserLockFilter(), BasicAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(new RestAuthenticationEntryPoint());
                    ex.accessDeniedHandler(customAccessDeniedHandler());
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable())
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/h2-console").permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(toH2Console()).permitAll()

                        .requestMatchers(HttpMethod.POST, SignupRoute.PATH)
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, ChangePasswordRoute.PATH)
                        .hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR")

                        .requestMatchers(HttpMethod.GET, PaymentRoute.PATH)
                        .hasAnyRole("USER", "ACCOUNTANT")

                        .requestMatchers(HttpMethod.POST, PaymentsRoute.PATH)
                        .hasRole("ACCOUNTANT")

                        .requestMatchers(HttpMethod.PUT, PaymentsRoute.PATH)
                        .hasRole("ACCOUNTANT")

                        .requestMatchers(HttpMethod.GET, UserRoute.PATH)
                        .hasRole("ADMINISTRATOR")

                        .requestMatchers(HttpMethod.DELETE, UserRoute.PATH + "**")
                        .hasRole("ADMINISTRATOR")

                        .requestMatchers(HttpMethod.PUT, RoleRoute.PATH)
                        .hasRole("ADMINISTRATOR")

                        .requestMatchers(HttpMethod.PUT, AccessRoute.PATH)
                        .hasRole("ADMINISTRATOR")

                        .requestMatchers(HttpMethod.GET, SecurityEventsRoute.PATH)
                        .hasRole("AUDITOR")

                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown")
                        .permitAll()

                        .anyRequest().denyAll()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                );

        return http.build();
    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(13);
//    }

    @Bean
    public CustomUserLockFilter customUserLockFilter() {
        return new CustomUserLockFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) {

                SecurityEvent securityEvent = new SecurityEvent();
                securityEvent.setDate(LocalDateTime.now());
                securityEvent.setAction("ACCESS_DENIED");
                securityEvent.setSubject(request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous");
                securityEvent.setObject(request.getRequestURI()); // Adjust as needed
                securityEvent.setPath(request.getRequestURI());

                securityRepository.save(securityEvent);


                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"timestamp\":\"<date>\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access Denied!\",\"path\":\"" + request.getRequestURI() + "\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}