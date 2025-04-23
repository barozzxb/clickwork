package vn.clickwork.service;

import java.util.List;
import java.util.Optional;

import vn.clickwork.entity.Account;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;

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
}
