package vn.clickwork.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name="Employer")
public class Employer extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="datefounded", columnDefinition="date")
	private LocalDate dob;
	
	@Column(name="website", columnDefinition="nvarchar(255)")
	private String website;
	
	@Column(name="taxnumber", columnDefinition="nvarchar(255)")
	private String taxnumber;
	
	@Column(name="field", columnDefinition="nvarchar(255)")
	private String field;
	
	@Column(name="workingdays", columnDefinition="nvarchar(255)")
	private String workingdays;
	
	@Column(name="companysize", columnDefinition="nvarchar(255)")
	private String companysize;
	
	@Column(name="sociallink", columnDefinition="nvarchar(255)")
	private String sociallink;
	
	@Column(name="overview", columnDefinition="nvarchar(5000)")
	private String overview;
	
	@OneToMany(mappedBy="employer", fetch = FetchType.LAZY)
	private List<Job> jobs;
}
