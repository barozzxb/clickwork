package vn.clickwork.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Address")
public class Address implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="nation", columnDefinition="nvarchar(255)")
	private String nation;
	
	@Column(name="province", columnDefinition="nvarchar(255)")
	private String province;
	
	@Column(name="district", columnDefinition="nvarchar(255)")
	private String district;
	
	@Column(name="village", columnDefinition="nvarchar(255)")
	private String village;
	
	@Column(name="detail", columnDefinition="nvarchar(255)")
	private String detail;
	
}
