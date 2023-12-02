package account.security;

import account.entity.AppUser;
import account.entity.LoginAttempt;
import account.entity.SecurityEvent;
import account.repository.LoginAttemptRepository;
import account.repository.SecurityRepository;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CustomAuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private SecurityRepository securityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName().toLowerCase();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = attrs != null ? attrs.getRequest().getRequestURI() : "Unknown";

        logSecurityEvent("LOGIN_FAILED", username, path);
        handleLoginAttempt(username, path);
    }

    private void logSecurityEvent(String action, String subject, String path) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setDate(LocalDateTime.now());
        securityEvent.setAction(action);
        securityEvent.setSubject(subject);
        securityEvent.setObject(path);
        securityEvent.setPath(path);
        securityRepository.save(securityEvent);
    }

    @Transactional
    private void handleLoginAttempt(String email, String path) {
        LoginAttempt loginAttempt = loginAttemptRepository.findById(email.toLowerCase())
                .orElse(new LoginAttempt(email, 0, LocalDateTime.now(), false));

        loginAttempt.incrementFailedAttempts();

        if (loginAttempt.getFailedAttempts() > 4) {

            Optional<AppUser> maybeAdmin = userRepository.findByEmail(email);
            boolean isAdmin = false;

            if (maybeAdmin.isPresent()) {
                AppUser maybierAdmin = maybeAdmin.get();

                isAdmin = maybierAdmin.getAdmin();
            }

            if (! isAdmin) {
                loginAttempt.setIsLocked(true);
            }

            loginAttempt.resetFailedAttempts();
            loginAttemptRepository.save(loginAttempt);
            logSecurityEvent("BRUTE_FORCE", email, path);

            SecurityEvent securityEvent = new SecurityEvent();
            securityEvent.setDate(LocalDateTime.now());
            securityEvent.setAction("LOCK_USER");
            securityEvent.setSubject(email);
            securityEvent.setObject("Lock user " + email);
            securityEvent.setPath(path);
            securityRepository.save(securityEvent);
        }

        loginAttempt.setLastAttemptTime(LocalDateTime.now());
        loginAttemptRepository.save(loginAttempt);
    }
}