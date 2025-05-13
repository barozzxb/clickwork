package vn.clickwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.CV;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

	List<CV> findByApplicant(Applicant applicant);


	CV findByApplicantAccountUsernameAndIsDefaultTrue(String username);

}
