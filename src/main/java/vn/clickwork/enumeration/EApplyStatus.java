package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum EApplyStatus {

	PENDING("Đang chờ"),
	ACCEPTED("Đã được chấp nhận"),
	REJECTED("Đã bị từ chối");
	
	private final String value;

	private EApplyStatus(String value) {
		this.value = value;
	}
}
