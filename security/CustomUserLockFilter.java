package account.security;

import account.entity.AppUser;
import account.entity.LoginAttempt;
import account.repository.LoginAttemptRepository;
import account.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class CustomUserLockFilter extends OncePerRequestFilter {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            String username = values[0].toLowerCase();

            Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findById(username.toLowerCase());

            if (loginAttemptOpt.isPresent() && loginAttemptOpt.get().getIsLocked()) {

                request.setAttribute("accountLocked", true);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied due to multiple failed login attempts.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
