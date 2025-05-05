package vn.clickwork.model.dto;

import java.sql.Timestamp;

public class ReportDTO {
    private Long id;
    private String title;
    private String content;
    private Timestamp sendat;
    private String status;
    private String senderName;
    private String senderEmail;
    private String reportedName;
    private String reportedEmail;

    // Getters, setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getSendat() { return sendat; }
    public void setSendat(Timestamp sendat) { this.sendat = sendat; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getReportedName() { return reportedName; }
    public void setReportedName(String reportedName) { this.reportedName = reportedName; }
    public String getReportedEmail() { return reportedEmail; }
    public void setReportedEmail(String reportedEmail) { this.reportedEmail = reportedEmail; }
}