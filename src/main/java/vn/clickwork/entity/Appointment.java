package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Appointment")
public class Appointment implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="createdat", columnDefinition="timestamp")
	private Timestamp createdat;
	
	@Column(name="place", columnDefinition="nvarchar(255)")
	private String place;
	
	@Column(name="time", columnDefinition="timestamp")
	private Timestamp time;
	
	@Column(name="website", columnDefinition="nvarchar(255)")
	private String website;
	
	//relationship
	
	@ManyToOne
	@JoinColumn(name="applicant_id", referencedColumnName = "username")
	private Applicant applicant;
	
	@ManyToOne
	@JoinColumn(name="employer_id", referencedColumnName = "username")
	private Employer employer;
	
	@ManyToOne
	@JoinColumn(name="job_id")
	private Job job;
}
