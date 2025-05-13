package vn.clickwork.controller.api.v1.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.Response;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;
import vn.clickwork.service.AccountService;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

	@Autowired
	AccountService accServ;

	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginModel) {
		return new ResponseEntity<Response>(accServ.login(loginModel), HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest registerModel) {
		return new ResponseEntity<Response>(accServ.register(registerModel), HttpStatus.OK);
	}
	
	@PostMapping("/active-account")
	public ResponseEntity<?> activeAccount(@RequestBody String username) {
		return accServ.activeAccount(username);
	}
}
