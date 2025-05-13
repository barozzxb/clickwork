package vn.clickwork.model.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobDTO {
	private Long id;
	private String name;
	private String jobtype;
	private Timestamp createdat;
	private String salary;
	private String address;
	private List<String> tags;
	private String description;
	private String requiredskill;
	private String benefit;
	private String field;
	private int quantity;
	private boolean isActive;
	private EmployerDTO employer;
}
