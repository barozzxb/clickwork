package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.enumeration.ENotiType;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Notification")
public class Notification implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//list user
	
	@Column(name="title", columnDefinition="nvarchar(255)")
	private String title;
	
	@Column(name="content", columnDefinition="nvarchar(5000)")
	private String content;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type", columnDefinition="nvarchar(255)")
	private ENotiType type;
	
	@Column(name="sendat", columnDefinition="timestamp")
	private Timestamp sendat;
	
	@Column(name="isRead", columnDefinition="boolean")
	private boolean isRead;
	//relationship
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name="applicantNoti",
			joinColumns = @JoinColumn(name="id"),
			inverseJoinColumns = @JoinColumn(name="username"))
    private List<Applicant> applicants;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
			name="employerNoti",
			joinColumns = @JoinColumn(name="id"),
			inverseJoinColumns = @JoinColumn(name="username"))
    private List<Employer> employers;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
			name="adminNoti",
			joinColumns = @JoinColumn(name="id"),
			inverseJoinColumns = @JoinColumn(name="username"))
    private List<Admin> admins;

}
