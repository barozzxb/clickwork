package vn.clickwork.service;

import java.util.List;
import java.util.Optional;

import vn.clickwork.entity.Account;

public interface AccountService {

	void delete(Account entity);

	void deleteById(String id);

	long count();

	Optional<Account> findById(String id);

	List<Account> findAll();

	<S extends Account> S save(S entity);

	Account login(String username, String pasword);
	
	boolean register(Account account);

	Optional<Account> findByUsername(String username);
}
