package vn.clickwork.model.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobFilterRequest {
    private String name;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Double salaryMin;
    private Double salaryMax;
    private List<String> tags;
    private Long employerId;
    private String jobType;
    // Thêm trường isActive để kiểm soát việc hiển thị công việc active/inactive
    private Boolean isActive;
}
