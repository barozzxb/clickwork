package vn.clickwork.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name="Employer")
public class Employer extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="datefounded", columnDefinition="date")
	private LocalDate datefounded;
	
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
	
	@OneToMany(mappedBy = "employer", fetch = FetchType.LAZY)
	private List<Photo> photos;
	
	//relationship
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employer")
	protected List<Address> addresses;
	
	@OneToMany(mappedBy="employer", fetch = FetchType.LAZY)
	private List<Job> jobs;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="username", referencedColumnName = "username")
	@JsonBackReference
	private Account account;
	
	@OneToMany(mappedBy="employer", fetch = FetchType.LAZY)
	private List<Appointment> appointments;
	
	@ManyToMany(mappedBy = "employers")
	private List<Notification> notifications;
	
	@OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Support> supports;
	
	@OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsSent;

    @OneToMany(mappedBy = "reportedemployer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reportsReceived;
}
