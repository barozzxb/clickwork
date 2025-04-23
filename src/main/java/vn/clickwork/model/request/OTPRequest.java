package vn.clickwork.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPRequest {

	String email;
	String inputOtp;
}
