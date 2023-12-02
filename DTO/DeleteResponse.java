package account.DTO;

public class DeleteResponse {

    private String user;
    private String status = "Deleted successfully!";

    public DeleteResponse(String user) {
        this.user = user;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String Status) {
        this.status = status;
    }
}
