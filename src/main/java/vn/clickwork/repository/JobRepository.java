package vn.clickwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
=======
import org.springframework.data.repository.query.Param;
>>>>>>> Stashed changes
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

	List<Job> findByTags(String tags);
<<<<<<< Updated upstream
=======

	List<Job> findBySave(SaveJob save);

	Optional<Job> findById(Long id);

	@Query("SELECT new vn.clickwork.model.dto.JobFieldCountDTO(j.field, COUNT(j)) FROM Job j GROUP BY j.field")
	List<JobFieldCountDTO> countJobsByField();

	long countByIsActiveTrue();

	long countByIsActiveFalse();

	@Query("SELECT j.jobtype AS type, COUNT(j) AS count FROM Job j GROUP BY j.jobtype")
	List<Object[]> countJobsByType();

	@Query("SELECT j FROM Job j WHERE j.employer.email = :email")
	List<Job> findByEmployerEmail(@Param("email") String email);
	
>>>>>>> Stashed changes
}