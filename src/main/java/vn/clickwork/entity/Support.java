package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.clickwork.enumeration.EResponseStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name="Support")
public class Support implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="title", columnDefinition="nvarchar(255)")
	private String title;
	
	@Column(name="content", columnDefinition="nvarchar(255)")
	private String content;
	
	@Column(name="sendat", columnDefinition="timestamp")
	private Timestamp sendat;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status", columnDefinition="nvarchar(255)")
	private EResponseStatus status;

	@Column(name="response", columnDefinition="nvarchar(500)")
	private String response;
	//relationship
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_applicant", referencedColumnName = "username")
    private Applicant applicant;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_employer", referencedColumnName = "username")
    private Employer employer;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_admin", referencedColumnName = "username")
    private Admin admin;
}
