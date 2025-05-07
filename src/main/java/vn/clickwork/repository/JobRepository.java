package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;

import vn.clickwork.model.dto.JobFieldCountDTO;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByTags(String tags);

	
  List<Job> findBySave(SaveJob save);
    
  Optional<Job> findById(Long id);

	@Query("SELECT new vn.clickwork.model.dto.JobFieldCountDTO(j.field, COUNT(j)) FROM Job j GROUP BY j.field")
	List<JobFieldCountDTO> countJobsByField();

	long countByIsActiveTrue();

	long countByIsActiveFalse();

	@Query("SELECT j.jobtype AS type, COUNT(j) AS count FROM Job j GROUP BY j.jobtype")
	List<Object[]> countJobsByType();

}