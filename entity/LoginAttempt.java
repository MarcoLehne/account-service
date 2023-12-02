package account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class LoginAttempt {

    @Id
    private String email;
    private int failedAttempts;
    private LocalDateTime lastAttemptTime;
    private boolean isLocked;

    public LoginAttempt() {}

    public LoginAttempt(String email, int failedAttempts, LocalDateTime lastAttemptTime, boolean isLocked) {
        this.email = email;
        this.failedAttempts = failedAttempts;
        this.lastAttemptTime = lastAttemptTime;
        this.isLocked = isLocked;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLastAttemptTime() {
        return lastAttemptTime;
    }

    public void setLastAttemptTime(LocalDateTime lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }
}
