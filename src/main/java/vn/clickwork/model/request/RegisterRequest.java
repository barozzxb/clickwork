package vn.clickwork.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.clickwork.enumeration.ERole;

@AllArgsConstructor
@Data
public class RegisterRequest {

	String username;
	String password;
	String email;
	ERole role;
	
}
