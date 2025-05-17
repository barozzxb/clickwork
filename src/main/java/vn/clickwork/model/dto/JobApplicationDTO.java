package vn.clickwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobApplicationDTO {
    private Long id;
    private ApplicantDTO applicant;
    private String status;
    private Timestamp appliedAt;
    private String jobName;
    private Long jobId;
    private String companyName;
}
