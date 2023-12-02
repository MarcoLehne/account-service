package account.controller;

import account.DTO.DeleteResponse;
import account.exception.Custom400Exception;
import account.route.v1.UserRoute;
import account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteUserController {

    private final UserService userservice;

    public DeleteUserController(UserService userService){
        this.userservice = userService;
    }

    @DeleteMapping(path = UserRoute.PATH + "{user}")
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String user) {
        userservice.deleteUser(user);

        return new ResponseEntity<>(new DeleteResponse(user), HttpStatus.OK);
    }

}
