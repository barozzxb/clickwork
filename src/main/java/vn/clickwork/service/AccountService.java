package vn.clickwork.service;

import java.util.List;
import java.util.Optional;

import vn.clickwork.entity.Account;
import vn.clickwork.model.LoginModel;
import vn.clickwork.model.RegisterModel;
import vn.clickwork.model.Response;

public interface AccountService {

	void delete(Account entity);

	void deleteById(String id);

	long count();

	Optional<Account> findById(String id);

	List<Account> findAll();

	<S extends Account> S save(S entity);

	Response login(LoginModel loginModel);
	
	Response register(RegisterModel model);

	Optional<Account> findByUsername(String username);
}
