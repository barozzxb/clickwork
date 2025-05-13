package vn.clickwork.model.dto;

import lombok.Data;

@Data
public class SavedJobDTO {
    private Long jobId;
    private String title;
    private String company;
    private String location;
    private String field;
    private String type;
    private Integer countApplicant;
    private String savedDate;
}
