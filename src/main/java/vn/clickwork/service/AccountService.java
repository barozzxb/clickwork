package vn.clickwork.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import vn.clickwork.entity.Account;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;
import vn.clickwork.model.request.ResetPasswordRequest;

public interface AccountService {

	void delete(Account entity);

	void deleteById(String id);

	long count();

	Optional<Account> findById(String id);

	List<Account> findAll();

	<S extends Account> S save(S entity);

	Response login(LoginRequest loginModel);
	
	Response register(RegisterRequest model);

	Optional<Account> findByUsername(String username);
	
	ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordRequest model);

	ResponseEntity<Response> requestResetPassword(String email);
}
