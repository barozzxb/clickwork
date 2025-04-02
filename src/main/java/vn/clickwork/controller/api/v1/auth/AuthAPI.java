package vn.clickwork.controller.api.v1.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.entity.Account;
import vn.clickwork.enumeration.ERole;
import vn.clickwork.model.Response;
import vn.clickwork.service.AccountService;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {

	@Autowired
	AccountService accServ;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam("username") String username,
											@RequestParam("password") String password){
		Account acc = accServ.login(username, password);
		if (acc != null) {
			return new ResponseEntity<Response>(new Response(true, "Đăng nhập thành công", acc), HttpStatus.OK);
		} else {
			return ResponseEntity.badRequest().body("Không tìm thấy tài khoản");
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestParam("username") String username,
											@RequestParam("password") String password,
											@RequestParam("role") ERole role){
		Account acc = new Account(username, password, role);
		boolean isRegistered = accServ.register(acc);
		if (isRegistered) {
			return new ResponseEntity<Response>(new Response(true, "Đăng kí thành công", acc), HttpStatus.OK);
		} else {
			return ResponseEntity.badRequest().body("Tài khoản đã tồn tại, vui lòng thử lại");
		}
	}
}
