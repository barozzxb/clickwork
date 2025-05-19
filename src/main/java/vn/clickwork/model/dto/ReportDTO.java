package vn.clickwork.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
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

}