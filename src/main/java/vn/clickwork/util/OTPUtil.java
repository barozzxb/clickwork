package vn.clickwork.util;

import org.springframework.stereotype.Component;

@Component
public class OTPUtil {
	
	
	public static String generateOTP() {
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			int digit = (int) (Math.random() * 10);
			otp.append(digit);
		}
		return otp.toString();
	}
	
	public static boolean validateOTP(String otp, String userInput) {
		return otp.equals(userInput);
	}
	
	public static boolean validateOTP(String otp, String userInput, int maxAttempts) {
		return otp.equals(userInput) && maxAttempts > 0;
	}
}
