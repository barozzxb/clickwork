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
public class UserStatsDTO {
    private long totalUsers;
    private List<Map<String, Object>> usersByRole;
    private List<Map<String, Object>> usersByStatus;
    private List<Map<String, Object>> registrationsByMonth;
}
