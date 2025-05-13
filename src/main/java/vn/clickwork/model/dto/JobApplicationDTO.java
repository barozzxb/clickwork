package vn.clickwork.model.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class JobApplicationDTO {
    private Long id;
    private ApplicantDTO applicant;
    private String status;
    private Timestamp appliedAt;
    private String jobName;
    private Long jobId;
}
