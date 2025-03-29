package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.enumeration.EApplyStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="JobApplication")
public class JobApplication implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="time", columnDefinition="timestamp")
	private Timestamp time;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status", columnDefinition="nvarchar(255)")
	private EApplyStatus status;
	
	@ManyToOne
	@JoinColumn(name="applicant_id", referencedColumnName = "username")
	private Applicant applicant;
	
	@ManyToOne
	@JoinColumn(name="job_id", referencedColumnName = "id")
	private Job job;
}
