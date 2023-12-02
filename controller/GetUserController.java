package account.controller;

import account.DTO.AppUserResponse;
import account.route.v1.UserRoute;
import account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetUserController {

    private final UserService userService;

    public GetUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = UserRoute.PATH)
    public ResponseEntity<List<AppUserResponse>> getUsers() {
        return new ResponseEntity<>(userService.retrieveAllUsers(), HttpStatus.OK);
    }
}
