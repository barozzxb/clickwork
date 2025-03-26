package vn.clickwork.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.enumeration.ERole;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Account")
public class Account implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="username", columnDefinition="varchar(255)")
	private String username;
	
	@Column(name="password", columnDefinition="varchar(255)", nullable=false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name="role", columnDefinition="varchar(255)", nullable=false)
	private ERole role;
}
