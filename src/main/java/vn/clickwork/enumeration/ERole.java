package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum ERole {

	ADMIN("Quản trị viên"),
	APPLICANT("Ứng viên"),
	EMPLOYER("Nhà tuyển dụng");

	private final String value;
	
	private ERole(String value) {
		this.value=value;
	}
	
}
