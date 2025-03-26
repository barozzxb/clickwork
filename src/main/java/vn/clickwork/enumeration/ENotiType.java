package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum ENotiType {

	INFORM("Thông báo"),
	SYSTEM("Thông báo hệ thống"),
	RESPONSE("Thông báo phản hồi");
	
	private final String value;

	private ENotiType(String value) {
		this.value = value;
	}
	
}
