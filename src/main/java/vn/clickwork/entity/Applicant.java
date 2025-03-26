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
@Table(name="Applicant")
public class Applicant extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="dob", columnDefinition="date")
	private LocalDate dob;
	
	@Enumerated(EnumType.STRING)
	@Column(name="gender", columnDefinition="nvarchar(255)")
	private String gender;
	
	@OneToMany(mappedBy="applicant", fetch = FetchType.LAZY)
	private List<CV> cvs;
	
	@Column(name="interested", columnDefinition="nvarchar(255)")
	private String interested;
	
	//relationship
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="username")
	private Account account;
}
