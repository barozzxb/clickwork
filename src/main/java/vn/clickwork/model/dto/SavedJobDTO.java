package vn.clickwork.model.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.entity.Job;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavedJobDTO {
	private Long id;
    private Long jobId;
    private String title;
    private String field;
    private String type;
//    private Integer countApplicant;
    private Timestamp savedDate;
}
