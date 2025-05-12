package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.enumeration.EJobType;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Job")
public class Job implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="name", columnDefinition="nvarchar(255)")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name="jobtype", columnDefinition="nvarchar(255)")
	private EJobType jobtype;

	@Column(name="createdat", columnDefinition="timestamp")
	private Timestamp createdat;

	@Column(name="salary", columnDefinition="nvarchar(255)")
	private String salary;

	@ElementCollection
	private List<String> tags;

	@Column(name="description", columnDefinition="nvarchar(5000)")
	private String description;

	@Column(name="requiredskill", columnDefinition="nvarchar(5000)")
	private String requiredskill;

	@Column(name="benefit", columnDefinition="nvarchar(5000)")
	private String benefit;
<<<<<<< Updated upstream
	
	@Column(name="filed", columnDefinition="nvarchar(255)")
=======

	@Column(name="field", columnDefinition="nvarchar(255)")
>>>>>>> Stashed changes
	private String field;

	@Column(name="quantity", columnDefinition="int")
	private int quantity;

	@Column(name="isactive", columnDefinition="boolean")
	private boolean isActive;
	//relationship

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="owner")
	@JsonManagedReference
	private Employer employer;

	@ManyToOne
	private SaveJob save;

	@OneToMany(mappedBy="job", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JobApplication> jobApplications;

	@OneToMany(mappedBy="job", cascade = CascadeType.ALL)
	private List<Appointment> appointments;

	@PrePersist
	protected void onCreate() {
		this.createdat = new Timestamp(System.currentTimeMillis());
	}
}
