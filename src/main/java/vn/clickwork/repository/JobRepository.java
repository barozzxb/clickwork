package vn.clickwork.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Job;
import vn.clickwork.model.dto.JobFieldCountDTO;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByTags(String tags);
	@Query("SELECT new vn.clickwork.model.dto.JobFieldCountDTO(j.field, COUNT(j)) FROM Job j GROUP BY j.field")
	List<JobFieldCountDTO> countJobsByField();

}
