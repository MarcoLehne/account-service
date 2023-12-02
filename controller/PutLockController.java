package account.controller;

import account.DTO.LockUnlockRequest;
import account.entity.LoginAttempt;
import account.entity.SecurityEvent;
import account.repository.LoginAttemptRepository;
import account.repository.SecurityRepository;
import account.route.v1.AccessRoute;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class PutLockController {

    private final LoginAttemptRepository loginAttemptRepository;
    private final SecurityRepository securityRepository;

    public PutLockController(LoginAttemptRepository loginAttemptRepository, SecurityRepository securityRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
        this.securityRepository = securityRepository;
    }


    @PutMapping(path = AccessRoute.PATH)
    public ResponseEntity<?> putUserLock(@RequestBody(required = false) LockUnlockRequest request, Authentication authentication) {
        String email = request.getUser().toLowerCase();
        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findById(email);

        String adminEmail = authentication.getName();

        if (loginAttemptOpt.isEmpty()) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Can't lock the ADMINISTRATOR!");
            errorResponse.put("path", AccessRoute.PATH);

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        LoginAttempt loginAttempt = loginAttemptOpt.get();
        boolean isLockOperation = request.getOperation().equalsIgnoreCase("LOCK");

        if (email.equals(adminEmail) && isLockOperation) {
            // Prevent locking the administrator
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            loginAttempt.setIsLocked(isLockOperation);
            loginAttemptRepository.save(loginAttempt);
        }

        // Log the security event
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setDate(LocalDateTime.now());
        securityEvent.setAction(isLockOperation ? "LOCK_USER" : "UNLOCK_USER");
        securityEvent.setSubject(isLockOperation ? email : adminEmail);
        securityEvent.setObject(isLockOperation ? "Lock user " + email : "Unlock user " + email);
        securityEvent.setPath(AccessRoute.PATH);
        securityRepository.save(securityEvent);

        // Response body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "User " + email + (isLockOperation ? " locked!" : " unlocked!"));

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
