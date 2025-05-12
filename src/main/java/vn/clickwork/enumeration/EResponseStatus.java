package vn.clickwork.enumeration;

import lombok.Getter;

@Getter
public enum EResponseStatus {

    RESPONDED("Đã phản hồi"),
    NO_RESPOND("Chưa phản hồi"),
    PENDING("Chờ xử lý"), // Thêm giá trị PENDING
	  DISMISSED("Đã từ chối");
	
    private final String value;

    private EResponseStatus(String value) {
        this.value = value;
    }
}
