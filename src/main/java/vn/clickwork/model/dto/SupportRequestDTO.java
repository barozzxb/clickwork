package vn.clickwork.model.dto;

import java.sql.Timestamp;
import vn.clickwork.enumeration.EResponseStatus;

public class SupportRequestDTO {
    private String title;
    private String content;
    private Timestamp sendat;
    private EResponseStatus status;
    private String applicantUsername;
    private String employerUsername;
    private String adminUsername;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getSendat() {
        return sendat;
    }

    public void setSendat(Timestamp sendat) {
        this.sendat = sendat;
    }

    public EResponseStatus getStatus() {
        return status;
    }

    public void setStatus(EResponseStatus status) {
        this.status = status;
    }

    public String getApplicantUsername() {
        return applicantUsername;
    }

    public void setApplicantUsername(String applicantUsername) {
        this.applicantUsername = applicantUsername;
    }

    public String getEmployerUsername() {
        return employerUsername;
    }

    public void setEmployerUsername(String employerUsername) {
        this.employerUsername = employerUsername;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
}