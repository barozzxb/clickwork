package vn.clickwork.model.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobSummaryDTO {
    private Long id;
    private String name;
    private String jobtype;
    private Timestamp createdat;
    private String salary;
    private String address;
    private List<String> tags;
    private String field;
    private boolean isActive;
    private EmployerSummaryDTO employer;

}
