package vn.clickwork.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Job;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.service.JobService;

@Service
public class JobServiceImpl implements JobService{
	
	@Autowired
	JobRepository jobRepo;

	@Override
	public <S extends Job> S save(S entity) {
		return jobRepo.save(entity);
	}

	@Override
	public List<Job> findAll() {
		return jobRepo.findAll();
	}

	@Override
	public Optional<Job> findById(Long id) {
		return jobRepo.findById(id);
	}

	@Override
	public long count() {
		return jobRepo.count();
	}

	@Override
	public void deleteById(Long id) {
		jobRepo.deleteById(id);
	}

	@Override
	public void delete(Job entity) {
		jobRepo.delete(entity);
	}

	@Override
	public List<Job> findByTags(String tag) {
		return jobRepo.findByTags(tag);
	}
	
	
}
