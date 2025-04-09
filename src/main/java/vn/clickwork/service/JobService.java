package vn.clickwork.service;

import java.util.List;
import java.util.Optional;

import vn.clickwork.entity.Job;

public interface JobService {

	List<Job> findByTags(String tags);

	void delete(Job entity);

	void deleteById(Long id);

	long count();

	Optional<Job> findById(Long id);

	List<Job> findAll();

	<S extends Job> S save(S entity);

}
