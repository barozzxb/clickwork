package vn.clickwork.model.dto;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.entity.Employer;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class JobDTO {

	private Long id;

	private String name;
	
	private String jobtype;
	
	private Timestamp createdat;
	
	private String salary;
	
	private List<String> tags;
	
	private String description;
	
	private String requiredskill;

	private String benefit;

	private String field;
	
	private int quantity;
	
	private boolean isActive;

	@JsonManagedReference
	private Employer employer;
}
