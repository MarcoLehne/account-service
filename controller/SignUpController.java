package account.controller;

import account.DTO.AppUserResponse;
import account.DTO.PasswordWasBreachedResponse;
import account.entity.SecurityEvent;
import account.DTO.UserExistResponse;
import account.entity.AppUser;
import account.exception.Custom400Exception;
import account.repository.RolesRepository;
import account.repository.SecurityRepository;
import account.repository.UserRepository;
import account.route.v1.SignupRoute;
import account.service.UserService;
import account.util.PasswordBreachChecker;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class SignUpController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityRepository securityRepository;

    public SignUpController(UserRepository userRepository,
                            RolesRepository rolesRepository,
                            PasswordEncoder passwordEncoder,
                            SecurityRepository securityRepository) {
        this.userService = new UserService(userRepository, rolesRepository, securityRepository);
        this.passwordEncoder = passwordEncoder;
        this.securityRepository = securityRepository;
    }

    @PostMapping(path = SignupRoute.PATH)
    public ResponseEntity<?> signUp(@Valid @RequestBody(required = false) AppUser appUser) {

        if (PasswordBreachChecker.isPasswordBreached(appUser.getPassword())) {
            return new ResponseEntity<>(new PasswordWasBreachedResponse(), HttpStatus.BAD_REQUEST);
        }

        String email = appUser.getEmail().toLowerCase();
        if (! email.matches("^(.+)@acme.com$")) {
            throw new Custom400Exception("Invalid email address");
        }

        appUser.setEmail(appUser.getEmail().toLowerCase());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser appUserOpt = userService.signUp(appUser);

        if (appUserOpt == null) {
            return new ResponseEntity<>(new UserExistResponse(), HttpStatus.BAD_REQUEST);
        } else {

            SecurityEvent securityEvent = new SecurityEvent();
            securityEvent.setDate(LocalDateTime.now());
            securityEvent.setAction("CREATE_USER");
            securityEvent.setSubject("Anonymous");
            securityEvent.setObject(appUser.getEmail());
            securityEvent.setPath(SignupRoute.PATH);

            securityRepository.save(securityEvent);


            return new ResponseEntity<>(
                    new AppUserResponse(
                            (int) appUserOpt.getId(),
                            appUserOpt.getName(),
                            appUserOpt.getLastname(),
                            appUserOpt.getEmail(),
                            appUserOpt.getRoles()
                    ), HttpStatus.OK);
        }
    }


}
