package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.clickwork.entity.Account;
import vn.clickwork.entity.Admin;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

	Admin findByEmail(String email);
	Admin findByAccountUsername(String username);
	Admin findByAccount(Account account);

	List<Admin> findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(String searchPattern, String searchPattern1);
}