package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.clickwork.enumeration.EJobType;
import vn.clickwork.model.dto.JobFieldCountDTO;
import vn.clickwork.model.dto.JobStatsDTO;

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

	@Query("SELECT j FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.addresses WHERE e.email = :email")
	List<Job> findByEmployerEmail(@Param("email") String email);

	@Query("SELECT j FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.addresses WHERE " +
			"(:name IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
			"(:jobType IS NULL OR j.jobtype = :jobType) AND " +
			"(:employerId IS NULL OR e.id = :employerId) AND " +
			"(:dateFrom IS NULL OR j.createdat >= :dateFrom) AND " +
			"(:dateTo IS NULL OR j.createdat <= :dateTo) AND " +
			"(:salaryMin IS NULL OR CAST(SUBSTRING(j.salary, 1, LOCATE('-', j.salary) - 1) AS double) >= :salaryMin) AND " +
			"(:salaryMax IS NULL OR CAST(SUBSTRING(j.salary, LOCATE('-', j.salary) + 1) AS double) <= :salaryMax) AND " +
			"(:isActive IS NULL OR j.isActive = :isActive)")
	List<Job> filterJobs(
			@Param("name") String name,
			@Param("jobType") EJobType jobType,
			@Param("employerId") Long employerId,
			@Param("dateFrom") java.time.LocalDate dateFrom,
			@Param("dateTo") java.time.LocalDate dateTo,
			@Param("salaryMin") Double salaryMin,
			@Param("salaryMax") Double salaryMax,
			@Param("isActive") Boolean isActive
	);

	@Query("SELECT DISTINCT j FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.addresses JOIN j.tags t WHERE t IN :tags")
	List<Job> findByTagsIn(@Param("tags") List<String> tags);

	@Query("SELECT j FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.addresses")
	List<Job> findAllWithEmployerAndAddresses();

	@Query("SELECT j FROM Job j JOIN FETCH j.employer e LEFT JOIN FETCH e.addresses")
	Page<Job> findAllWithEmployerAndAddresses(Pageable pageable);

	@Query("SELECT FUNCTION('DATE_FORMAT', j.createdat, '%Y-%m') as month, COUNT(j) as count " +
		   "FROM Job j " +
		   "GROUP BY FUNCTION('DATE_FORMAT', j.createdat, '%Y-%m') " +
		   "ORDER BY month")
	List<Object[]> countJobsByMonth();

}