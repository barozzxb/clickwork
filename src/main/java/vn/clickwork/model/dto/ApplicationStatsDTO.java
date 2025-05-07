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
public class ApplicationStatsDTO {
    private long totalApplications;
    private List<Map<String, Object>> applicationsByStatus;
    private List<Map<String, Object>> applicationsByMonth;
}
