package account.DTO;

import account.route.v1.PaymentsRoute;

public class UserNotExistResponse extends Response{

    public UserNotExistResponse() {
        setMessage("User does not exist!");
        setPath(PaymentsRoute.PATH);
    }

}
