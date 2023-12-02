package account.DTO;

import account.route.v1.UserRoute;

public class AdministratorNotRemoveResponse extends Response{

    public AdministratorNotRemoveResponse(String path) {
        setMessage("Can't remove ADMINISTRATOR role!");
        setPath(path);
    }
}
