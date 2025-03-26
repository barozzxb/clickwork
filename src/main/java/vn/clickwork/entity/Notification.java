package vn.clickwork.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.enumeration.ENotiType;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="Notification")
public class Notification implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//list user
	
	@Column(name="title", columnDefinition="nvarchar(255)")
	private String title;
	
	@Column(name="content", columnDefinition="nvarchar(5000)")
	private String content;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type", columnDefinition="nvarchar(255)")
	private ENotiType type;
	
	@Column(name="sendat", columnDefinition="timestamp")
	private Timestamp sendat;
}
