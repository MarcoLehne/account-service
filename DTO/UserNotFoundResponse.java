package account.DTO;

public class UserNotFoundResponse extends Response{
    public UserNotFoundResponse(){

        setStatus(404);
        setMessage("User not found!");
    }
}
