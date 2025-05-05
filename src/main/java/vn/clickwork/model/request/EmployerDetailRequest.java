package vn.clickwork.model.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clickwork.entity.Photo;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployerDetailRequest {

	private Long id;
	
	String fullname;
	String email;
	String phonenum;
	
	private LocalDate datefounded;
	
	private String website;
	
	private String taxnumber;
	
	private String field;
	
	private String workingdays;

	private String companysize;

	private String sociallink;

	private String overview;

	private List<Photo> photos;
}
