package vn.clickwork.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.service.impl.JobSpecification;

public interface JobService {

	ResponseEntity<Response> findByTags(String tags);

	void delete(Job entity);

	void deleteById(Long id);

	long count();

	ResponseEntity<Response> findById(Long id);

	ResponseEntity<Response> findAll();
	
	ResponseEntity<Response> findAll(Specification<Job> spec);

	ResponseEntity<Response> save(Job entity);
	
	ResponseEntity<Response> updateJob(Job entity);

	ResponseEntity<Response> findNewJobs();

<<<<<<< Updated upstream
=======
	ResponseEntity<Response> filterJobs(JobFilterRequest request);

	ResponseEntity<Response> findByEmployerEmail(String email);

	ResponseEntity<Response> toggleJobStatus(Long id);

>>>>>>> Stashed changes
}
