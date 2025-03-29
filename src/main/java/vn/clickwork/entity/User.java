package vn.clickwork.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@MappedSuperclass
public abstract class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="fullname", columnDefinition="nvarchar(255)")
	protected String fullname;
	
	@Column(name="email", columnDefinition="nvarchar(255)")
	protected String email;
	
	@Column(name="phonenum", columnDefinition="nvarchar(255)")
	protected String phonenum;
	
	@Column(name="avatar", columnDefinition="nvarchar(255)")
	protected String avatar;
	
}
