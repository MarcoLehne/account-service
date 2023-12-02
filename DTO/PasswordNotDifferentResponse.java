package account.DTO;

import account.route.v1.ChangePasswordRoute;

public class PasswordNotDifferentResponse extends Response {
    public PasswordNotDifferentResponse() {
        setMessage("The passwords must be different!");
        setPath(ChangePasswordRoute.PATH);
    }
}
