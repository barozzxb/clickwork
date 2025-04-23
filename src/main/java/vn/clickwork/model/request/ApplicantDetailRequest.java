package vn.clickwork.model.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.clickwork.enumeration.EGender;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ApplicantDetailRequest {

	Long id;
	String fullname;
	String email;
	String phonenum;
	
	LocalDate dob;
	EGender gender;
	String interested;

	
}
