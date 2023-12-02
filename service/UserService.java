package account.service;

import account.DTO.AppUserResponse;
import account.DTO.ChangeRolesRequest;
import account.entity.SecurityEvent;
import account.entity.AppUser;
import account.entity.Role;
import account.exception.*;
import account.repository.RolesRepository;
import account.repository.SecurityRepository;
import account.repository.UserRepository;
import account.util.Roles;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final SecurityRepository securityRepository;

    private final RolesService rolesService;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository, SecurityRepository securityRepository) {

        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.rolesService = new RolesService(userRepository, rolesRepository);
        this.securityRepository = securityRepository;
    }

    @Transactional
    public AppUser signUp(AppUser appUser) {

        if (userRepository.findByEmail(appUser.getEmail()).isEmpty()) {
            appUser.generateUsername();

            if (userRepository.count() == 0) {
                Optional<Role> optRole = rolesRepository.findByRole(Roles.Constants.ADMINISTRATOR);
                appUser.setAdmin(true);
                appUser.addRole(optRole.get());
            } else {
                Optional<Role> optRole = rolesRepository.findByRole(Roles.Constants.USER);
                appUser.setAdmin(false);
                appUser.addRole(optRole.get());
            }

            AppUser finishedAppUser = userRepository.save(appUser);
            return appUser;
        } else {
            return null;
        }
    }

    public List<AppUserResponse> retrieveAllUsers() {
        List<AppUserResponse> allUsers = new ArrayList<>();

        Iterable<AppUser> users = userRepository.findAll();

        for (AppUser user : users) {
            allUsers.add(
                    new AppUserResponse(
                            (int) user.getId(),
                            user.getName(),
                            user.getLastname(),
                            user.getEmail(),
                            user.getRoles()
                    )
            );
        }

        return allUsers;
    }

    public AppUserResponse retrieveUser(String username) {

        Optional<AppUser> optAppUser = userRepository.findUserByUsername(username);

        if (optAppUser.isEmpty()) {
            throw new UserNotExistException();
        }

        AppUser appUser = optAppUser.get();

        return new AppUserResponse(
                (int) appUser.getId(),
                appUser.getName(),
                appUser.getLastname(),
                appUser.getEmail(),
                appUser.getRoles()
        );
    }

    @Transactional
    public void putRole(ChangeRolesRequest changeRolesRequest) {


        if (! Roles.getRolesAsString().contains("ROLE_" + changeRolesRequest.getRole())) {
            throw new NotFoundException();
        }

        changeRolesRequest.setUser(changeRolesRequest.getUser().toLowerCase());

        Optional<AppUser> optionalAppUser = userRepository.findUserByUsername(changeRolesRequest.getUser());

        if (optionalAppUser.isEmpty()) {
            throw new Custom404Exception("User not found!");
        }

        AppUser appUser = optionalAppUser.get();

        Optional<Role> optionalRole = rolesRepository.findByRole(changeRolesRequest.getPrefixedRole());
        Role role = optionalRole.get();

        if (changeRolesRequest.getOperation().equals("GRANT")) {

//            if (changeRolesRequest.getRole().equals(Roles.Constants.ADMINISTRATOR)) {
//                throw new Custom400Exception("Can't grant ADMINISTRATOR role!");
//            }

            if(("ROLE_" + changeRolesRequest.getRole()).equals(Roles.Constants.ADMINISTRATOR)) {
                throw new Custom400Exception("The user cannot combine administrative and business roles!");
            }

            if (appUser.getRolesAsString().contains(Roles.Constants.ADMINISTRATOR)) {
                throw new Custom400Exception("The user cannot combine administrative and business roles!");
            }

            appUser.addRole(role);
            role.addUser(appUser);

        } else {

            if (! appUser.getRolesAsString().contains("ROLE_" + changeRolesRequest.getRole())) {
                throw new Custom400Exception("The user does not have a role!");
            }

            if (appUser.getRolesAsString().contains(Roles.Constants.ADMINISTRATOR)) {
                throw new Custom400Exception("Can't remove ADMINISTRATOR role!");
            }

            if (appUser.getRolesAsString().size() == 1) {
                throw new Custom400Exception("The user must have at least one role!");
            }

            appUser.removeRole(role);
            role.removeUser(appUser);
        }

        Optional<AppUser> adminOpt = userRepository.findUserByRoleName(Roles.Constants.ADMINISTRATOR);
        String adminEmail = adminOpt.map(AppUser::getEmail).orElse("Unknown");

        String operationString = changeRolesRequest.getOperation().equals("GRANT") ? "Grant" : "Remove";
        String roleWithoutPrefix = changeRolesRequest.getRole();  // Assuming this does not contain 'ROLE_'
        String preposition = changeRolesRequest.getOperation().equals("GRANT") ? " to " : " from ";

                String objectString = operationString + " role " + roleWithoutPrefix + preposition + changeRolesRequest.getUser();


        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setDate(LocalDateTime.now());
        securityEvent.setAction(changeRolesRequest.getOperation().equals("GRANT") ? "GRANT_ROLE" : "REMOVE_ROLE");
        securityEvent.setSubject(adminEmail);
        securityEvent.setObject(objectString);
        securityEvent.setPath("/api/admin/user/role");

        securityRepository.save(securityEvent);
    }

    @Transactional
    public void deleteUser(String email) {

        if (! email.matches("^.+@acme.com$")) {
            throw new EmailNotFoundException();
        }

        Optional<AppUser> optionalAppUser = userRepository.findUserByUsername(email);

        if (optionalAppUser.isEmpty()) {
            throw new UserNotExistException();
        }
        AppUser tbdAppUser = optionalAppUser.get();

        if (tbdAppUser.getRolesAsString().contains(Roles.Constants.ADMINISTRATOR)) {
            throw new AdministratorNotRemoveException();
        }

        for (Role role: tbdAppUser.getRoles()) {
            role.removeUser(tbdAppUser);
        }

        userRepository.delete(tbdAppUser);



        Optional<AppUser> adminOpt = userRepository.findUserByRoleName(Roles.Constants.ADMINISTRATOR);
        String adminEmail = adminOpt.map(AppUser::getEmail).orElse("Unknown");

        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setDate(LocalDateTime.now());
        securityEvent.setAction("DELETE_USER");
        securityEvent.setSubject(adminEmail);
        securityEvent.setObject(email);
        securityEvent.setPath("/api/admin/user/" + email);

        securityRepository.save(securityEvent);
    }
}
