package vn.clickwork.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

	private final PasswordEncoder passwordEncoder;

	public PasswordUtil(PasswordEncoder passwordEncoder) {
		super();
		this.passwordEncoder = passwordEncoder;
	}
	
	public String hashPassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
	
	public boolean verifyPassword(String rawPassword, String hashedPassword) {
		return passwordEncoder.matches(rawPassword, hashedPassword);
	}
}
