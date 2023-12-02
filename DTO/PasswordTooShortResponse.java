package account.DTO;

import account.route.v1.ChangePasswordRoute;

public class PasswordTooShortResponse extends Response{

    public PasswordTooShortResponse() {

        setPath(ChangePasswordRoute.PATH);
        setMessage("Password length must be 12 chars minimum!");
    }
}
