package vn.clickwork.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

import java.sql.Timestamp;

@Data
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
    private Timestamp suspendedUntil;
}