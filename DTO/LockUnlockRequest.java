package account.DTO;

public class LockUnlockRequest {
    private String user;
    private String operation;

    public LockUnlockRequest() {

    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
