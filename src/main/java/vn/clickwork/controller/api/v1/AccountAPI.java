package vn.clickwork.controller.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.entity.Account;
import vn.clickwork.model.Response;
import vn.clickwork.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountAPI {
	
	@Autowired
	AccountService accServ;
	
	@GetMapping
	public ResponseEntity<List<Account>> getAll(){
		return ResponseEntity.ok().body(accServ.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getAccount(@PathVariable String id){
		Optional<Account> acc = accServ.findById(id);
		if (acc.isPresent())
			return new ResponseEntity<Response>(new Response(true, "Lấy thông tin thành công", accServ.findById(id)), HttpStatus.OK);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body("Tài khoản không tồn tại");
	}
	
}
