package account.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordChangeRequest {

    @JsonProperty("new_password")
    private String newPassword;

    public PasswordChangeRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    public PasswordChangeRequest() { };

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
