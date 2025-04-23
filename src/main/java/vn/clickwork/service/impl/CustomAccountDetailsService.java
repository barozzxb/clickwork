package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Account;
import vn.clickwork.model.AccountDetails;
import vn.clickwork.repository.AccountRepository;

@Service
public class CustomAccountDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với username: " + username));
        return new AccountDetails(account);
	}

}
