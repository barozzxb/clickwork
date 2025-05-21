package vn.clickwork.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ApplicantProfileDTO {

	private Long id;
	protected String fullname;
	protected String email;
	protected String phonenum;
	protected String avatar;
	private LocalDate dob;
    private String gender;
    private List<CVDTO> cvs;
    private String interested;
    private List<AddressDTO> addresses;
    private String username;
}
