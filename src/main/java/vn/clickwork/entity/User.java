package vn.clickwork.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


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
	
	@Column(name="address", columnDefinition="nvarchar(255)")
	protected String address;
	
	@Column(name="avatar", columnDefinition="nvarchar(255)")
	protected String avatar;
}
