package account.DTO;

public class PasswordWasBreachedResponse extends Response {

    public PasswordWasBreachedResponse() {
        setMessage("The password is in the hacker's database!");
    }

}
