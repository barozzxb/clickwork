package vn.clickwork.model.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {

	private String username;
	private String oldPassword;
	private String newPassword;
	
}
