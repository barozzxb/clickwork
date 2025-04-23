package vn.clickwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class OTPCode {

	private String code;
	private long createdTime;
	private int maxAttempts;
}
