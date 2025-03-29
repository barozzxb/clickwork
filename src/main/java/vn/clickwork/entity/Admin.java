package vn.clickwork.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="Admin")
public class Admin extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	//relationship
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "admin")
	protected List<Address> addresses;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="username", referencedColumnName = "username")
	private Account account;
	
	@ManyToMany(mappedBy = "admins")
	private List<Notification> notifications;
	
	@OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Support> supports;
}
