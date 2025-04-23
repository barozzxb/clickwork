package vn.clickwork.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vn.clickwork.entity.Account;
import vn.clickwork.enumeration.ERole;

import java.util.Collection;
import java.util.List;

public class AccountDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final Account account;

    public AccountDetails(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Chuyển ERole thành dạng ROLE_XXX (ví dụ: ROLE_ADMIN)
        ERole role = account.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    // Các phương thức còn lại có thể trả về true hoặc theo yêu cầu nghiệp vụ của bạn
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
