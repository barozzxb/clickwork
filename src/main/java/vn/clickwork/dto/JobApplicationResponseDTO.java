package vn.clickwork.dto;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class JobApplicationResponseDTO {
    private String jobTitle;
    private Timestamp time;
    private String status;
}
