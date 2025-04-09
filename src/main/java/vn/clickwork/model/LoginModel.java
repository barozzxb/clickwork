package vn.clickwork.model;

import lombok.Data;
import vn.clickwork.enumeration.ERole;

@Data
public class LoginModel {

	String username;
	String password;
	boolean remember;
}
