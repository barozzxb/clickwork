package vn.clickwork.controller.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.Response;
import vn.clickwork.model.request.ResetPasswordRequest;
import vn.clickwork.service.AccountService;

@RestController
@RequestMapping("/api/reset-password")
public class ResetPassword {

	@Autowired
	AccountService accServ;
	
	@PostMapping("/request")
	public ResponseEntity<Response> requestResetPassword(@RequestBody String email) {
		return accServ.requestResetPassword(email);
	}
	
	@PostMapping
	public ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordRequest resetPasswordModel) {
		return accServ.resetPassword(resetPasswordModel);
	}
}
