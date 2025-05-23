package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum EAccountStatus {
    ACTIVE ("Đang hoạt động"),
    INACTIVE ("Ngừng hoạt động"),
    SUSPENDED ("Tạm ngưng");
    
    private final String value;
    
	private EAccountStatus(String value) {
		this.value = value;
	}
}
