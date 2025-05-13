package vn.clickwork.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private String title;
    private String content;
    private String reportedUsername; // Username của người bị báo cáo
}
