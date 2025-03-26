package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
	
	//photos
	
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
	
	@Column(name="filed", columnDefinition="nvarchar(255)")
	private String field;
	
	@Column(name="quantity", columnDefinition="int")
	private int quantity;
	
	//relationship
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="owner")
	private Employer employer;
}
