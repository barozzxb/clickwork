package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum EGender {

	MALE("Nam"),
	FEMALE("Nữ");
	
	private final String value;

	private EGender(String value) {
		this.value = value;
	}
	
}
