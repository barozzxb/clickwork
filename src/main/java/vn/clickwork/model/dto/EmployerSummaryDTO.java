package vn.clickwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployerSummaryDTO {
    private Long id;
    private String fullname;
    private String email;
    private String logo;
    private String website;
    private String mainAddress;
}
