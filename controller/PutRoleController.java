package account.controller;

import account.DTO.AppUserResponse;
import account.DTO.ChangeRolesRequest;
import account.entity.AppUser;
import account.entity.Role;
import account.route.v1.RoleRoute;
import account.repository.RolesRepository;
import account.repository.UserRepository;
import account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PutRoleController {

    UserRepository userRepository;
    RolesRepository rolesRepository;
    UserService userService;

    public PutRoleController(UserRepository userRepository, RolesRepository rolesRepository, UserService userService) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.userService = userService;
    }

    @PutMapping(path = RoleRoute.PATH)
    public ResponseEntity<AppUserResponse> putRole(@RequestBody(required = false) ChangeRolesRequest changeRolesRequest) {

        userService.putRole(changeRolesRequest);

        return new ResponseEntity<>(userService.retrieveUser(changeRolesRequest.getUser()), HttpStatus.OK);
    }
}
