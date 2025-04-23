package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum EJobType {

	FULLTIME("Toàn thời gian"),
	PARTTIME("Bán thời gian"),
	ONLINE("Trực tuyến"),
	FLEXIBLE("Linh hoạt"),
	INTERNSHIP("Thực tập");
	
	private final String value;

	private EJobType(String value) {
		this.value = value;
	}
	
}
