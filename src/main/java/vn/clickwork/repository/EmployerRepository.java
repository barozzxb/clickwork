package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Employer;

import java.util.List;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
	Employer findByEmail(String email);
	
	Employer findByAccount(Account account);

    List<Employer> findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(String searchPattern, String searchPattern1);
}
