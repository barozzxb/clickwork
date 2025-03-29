package vn.clickwork.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.clickwork.enumeration.EGender;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name="Applicant")
public class Applicant extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="dob", columnDefinition="date")
	private LocalDate dob;
	
	@Enumerated(EnumType.STRING)
	@Column(name="gender", columnDefinition="nvarchar(255)")
	private EGender gender;
	
	@OneToMany(mappedBy="applicant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CV> cvs;
	
	@Column(name="interested", columnDefinition="nvarchar(255)")
	private String interested;
	
	//relationship
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "applicant")
	protected List<Address> addresses;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="username", referencedColumnName = "username")
	private Account account;
	
	@OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL)
	private SaveJob savedjobs;
	
	@OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JobApplication> jobApplications;
	
	@OneToMany(mappedBy="applicant")
	private List<Appointment> appointments;
	
	@ManyToMany(mappedBy = "applicants")
	private List<Notification> notifications;
	
	@OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Support> supports;
	
	@OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsSent;

    @OneToMany(mappedBy = "reportedapplicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsReceived;
}
