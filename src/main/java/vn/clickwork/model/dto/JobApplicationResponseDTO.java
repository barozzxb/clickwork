package vn.clickwork.model.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class JobApplicationResponseDTO {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String company;
    private Timestamp appliedAt;
    private String status;
}