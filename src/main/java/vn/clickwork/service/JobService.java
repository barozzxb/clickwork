package vn.clickwork.service;

import org.springframework.http.ResponseEntity;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.model.request.PageRequest;

public interface JobService {

	ResponseEntity<Response> findByTags(String tags);

	void delete(Job entity);

	void deleteById(Long id);

	long count();

	ResponseEntity<Response> findById(Long id);

	ResponseEntity<Response> findAll();

	ResponseEntity<Response> findAllPaged(PageRequest pageRequest);

	ResponseEntity<Response> save(Job entity);

	ResponseEntity<Response> updateJob(Job entity);

	ResponseEntity<Response> findNewJobs();

	ResponseEntity<Response> filterJobs(JobFilterRequest request);

	ResponseEntity<Response> findByEmployerEmail(String email);

	ResponseEntity<Response> toggleJobStatus(Long id);
}
