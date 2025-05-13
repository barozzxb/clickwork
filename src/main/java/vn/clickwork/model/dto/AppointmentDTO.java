package vn.clickwork.model.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AppointmentDTO {
    private Long id;
    private Timestamp time;
    private String place;
    private String website;
    private String jobName;
    private String applicantName;
}
