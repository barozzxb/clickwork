package vn.clickwork.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CV")
public class CV implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="name", columnDefinition="nvarchar(255)")
	private String name;
	
	@Column(name="file", columnDefinition="nvarchar(255)")
	private String file;
	
	//relationship
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="username")
	private Applicant applicant;
	
}
