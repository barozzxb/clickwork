package vn.clickwork.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Account;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	AccountRepository accRepo;

	public <S extends Account> S save(S entity) {
		return accRepo.save(entity);
	}

	@Override
	public List<Account> findAll() {
		return accRepo.findAll();
	}

	@Override
	public Optional<Account> findById(String id) {
		return accRepo.findById(id);
	}

	@Override
	public long count() {
		return accRepo.count();
	}

	@Override
	public void deleteById(String id) {
		accRepo.deleteById(id);
	}

	@Override
	public void delete(Account entity) {
		accRepo.delete(entity);
	}

	@Override
	public Optional<Account> findByUsername(String username) {
		return accRepo.findByUsername(username);
	}

	@Override
	public Account login(String username, String pasword) {
		Optional<Account> acc = this.findByUsername(username);
		if (acc.isPresent()) {
			return acc.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean register(Account account) {
		Optional<Account> acc = this.findByUsername(account.getUsername());
		if (acc.isPresent()) {
			return false;
		} else {
			accRepo.save(account);
			return true;
		}
	}
	
	
}
