package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import vn.clickwork.enumeration.EAccountStatus;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition="varchar(255)", nullable=false)
	private EAccountStatus status;

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "suspended_until")
	private Timestamp suspendedUntil;

	public Account(String username, String password, ERole role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
	}

	//relationship

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Applicant applicant;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Employer employer;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Admin admin;

}