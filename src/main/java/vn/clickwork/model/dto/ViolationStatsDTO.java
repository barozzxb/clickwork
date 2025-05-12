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
public class ViolationStatsDTO {
    private long totalViolations;
    private List<Map<String, Object>> violationsByStatus;
    private List<Map<String, Object>> violationsByMonth;
}
