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
public class StatisticsDTO {
    private JobStatsDTO jobStats;
    private ApplicationStatsDTO applicationStats;
    private UserStatsDTO userStats;
    private List<Map<String, Object>> jobCategories;
    private ViolationStatsDTO violationStats;
}

