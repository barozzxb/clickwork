package vn.clickwork.model.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobFilterRequest {
    private String name;
    private Date dateFrom;
    private Date dateTo;
    private Integer salaryMin;
    private Integer salaryMax;
    private Long employerId;
    private String jobType;
    private String field;
    private Boolean isActive;
}
