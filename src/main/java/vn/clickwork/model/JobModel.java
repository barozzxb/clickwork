package vn.clickwork.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.clickwork.entity.Employer;

@NoArgsConstructor

@Getter
@Setter
public class JobModel {

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
