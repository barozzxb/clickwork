package vn.clickwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStatsDTO {
    private long totalJobs;
    private long activeJobs;
    private long inactiveJobs;
    private List<Map<String, Object>> jobsByType;
    private List<Map<String, Object>> jobsByMonth;
}
