package vn.clickwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.clickwork.enumeration.ERole;

@AllArgsConstructor
@Data
public class RegisterModel {

	String username;
	String password;
	String email;
	ERole role;
	
}
