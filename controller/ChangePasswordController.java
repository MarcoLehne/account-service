package account.controller;

import account.DTO.*;
import account.entity.AppUser;
import account.entity.LoginAttempt;
import account.entity.SecurityEvent;
import account.repository.LoginAttemptRepository;
import account.repository.SecurityRepository;
import account.repository.UserRepository;
import account.route.v1.ChangePasswordRoute;
import account.util.PasswordBreachChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class ChangePasswordController {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final SecurityRepository securityRepository;

    public ChangePasswordController(UserRepository userRepository, LoginAttemptRepository loginAttemptRepository, SecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.securityRepository = securityRepository;
    }

    @PostMapping(path = ChangePasswordRoute.PATH)
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody(required = false) PasswordChangeRequest passwordChangeRequest) {
        String username = authentication.getName().toLowerCase();
        String newPassword = passwordChangeRequest.getNewPassword();

        if (newPassword.length() < 12) {
            return new ResponseEntity<>(new PasswordTooShortResponse(), HttpStatus.BAD_REQUEST);
        }

        if (PasswordBreachChecker.isPasswordBreached(newPassword)) {
            return new ResponseEntity<>(new PasswordWasBreachedResponse(), HttpStatus.BAD_REQUEST);
        }

        Optional<AppUser> appUserOpt = userRepository.findUserByUsername(username);
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findById(username);

        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();

            if (loginAttemptOpt.isPresent()) {
                LoginAttempt loginAttempt = loginAttemptOpt.get();
                loginAttempt.resetFailedAttempts();
                loginAttemptRepository.save(loginAttempt);

            }

            if (appUser.getPassword().equals(newPassword)) {
                return new ResponseEntity<>(new PasswordNotDifferentResponse(), HttpStatus.BAD_REQUEST);
            }

            appUser.setPassword(newPassword); // Ensure to hash the password if required
            userRepository.save(appUser);

            SecurityEvent securityEvent = new SecurityEvent();
            securityEvent.setDate(LocalDateTime.now());
            securityEvent.setAction("CHANGE_PASSWORD");
            securityEvent.setSubject(appUser.getEmail());
            securityEvent.setObject(appUser.getEmail());
            securityEvent.setPath(ChangePasswordRoute.PATH);
            securityRepository.save(securityEvent);

            return new ResponseEntity<>(new PasswordChangeSuccessfulResponse(appUser.getEmail()), HttpStatus.OK);
        }

        return new ResponseEntity<>(new UserNotFoundResponse(), HttpStatus.BAD_REQUEST);
    }
}
