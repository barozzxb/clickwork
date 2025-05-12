package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

import org.springframework.data.jpa.repository.Query;

import vn.clickwork.model.dto.JobFieldCountDTO;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

	List<Job> findByTags(String tags);

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

	// Add query methods for filtering
	@Query("SELECT j FROM Job j WHERE " +
			"(:name IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
			"(:jobType IS NULL OR j.jobtype = :jobType) AND " +
			"(:field IS NULL OR j.field = :field) AND " +
			"(:minSalary IS NULL OR CAST(SUBSTRING(j.salary, 1, LOCATE('-', j.salary) - 1) AS int) >= :minSalary) AND " +
			"(:maxSalary IS NULL OR CAST(SUBSTRING(j.salary, LOCATE('-', j.salary) + 1) AS int) <= :maxSalary) AND " +
			"(:isActive IS NULL OR j.isActive = :isActive)")
	List<Job> filterJobs(
			@Param("name") String name,
			@Param("jobType") String jobType,
			@Param("field") String field,
			@Param("minSalary") Integer minSalary,
			@Param("maxSalary") Integer maxSalary,
			@Param("isActive") Boolean isActive
	);
}
