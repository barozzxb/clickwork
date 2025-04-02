package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long>{

}
