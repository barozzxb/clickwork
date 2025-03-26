package vn.clickwork.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name="Admin")
public class Admin extends User implements Serializable{

	private static final long serialVersionUID = 1L;

	
}
