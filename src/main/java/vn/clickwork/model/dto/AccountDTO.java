package vn.clickwork.model.dto;

import java.time.LocalDateTime;

public class AccountDTO {
    private String username;
    private String fullName;
    private String email;
    private String phoneNum;
    private String avatar;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private long violationCount;

    // Getters, setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public long getViolationCount() { return violationCount; }
    public void setViolationCount(long violationCount) { this.violationCount = violationCount; }
}