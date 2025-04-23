package vn.clickwork.controller.api.v1;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.Response;
import vn.clickwork.model.request.OTPRequest;
import vn.clickwork.service.impl.EmailService;

@RestController
@RequestMapping("/api/auth")
public class VerifyOTPAPI {

	private EmailService emailService;
	
	public VerifyOTPAPI(EmailService emailService) {
		this.emailService = emailService;
	}
	@PostMapping("/sendotp")
	  public ResponseEntity<Response> sendOTPEmail(@RequestBody Map<String,String> body) {
	    String email = body.get("email");
	    emailService.sendOTPEmail(email);
	    return ResponseEntity.ok(new Response(true, "Đã gửi OTP", null));
	  }

	  @PostMapping("/verifyotp")
	  public ResponseEntity<Response> verifyOTP(@RequestBody OTPRequest otpModel) {
	    return emailService.verifyOTP(otpModel);
	  }
}
