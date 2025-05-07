package vn.clickwork.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;

import java.util.List;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long>{
	
	Applicant findByEmail(String email);
	
	Applicant findByAccount(Account account);

  Applicant findByAccount_Username(String username);

  List<Applicant> findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(String searchPattern, String searchPattern1);

}
