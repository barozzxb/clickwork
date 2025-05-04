package vn.clickwork.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.service.JobService;

@Service
public class JobServiceImpl implements JobService{
	
	@Autowired
	JobRepository jobRepo;

	@Override
	public ResponseEntity<Response> save(Job entity) {
		try {
			jobRepo.save(entity);
			return new ResponseEntity<Response>(new Response(true, "Cập nhật công việc thành công", entity), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Response>(new Response(false, "Cập nhật công việc thất bại", null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Response> findAll() {
		List<Job> jobs = jobRepo.findAll();
		if (jobRepo.findAll().isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Danh sách công việc trống", null), HttpStatus.OK);
		}
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> findById(Long id) {
		Optional<Job> optJob = jobRepo.findById(id);
		if (optJob.isPresent()) {
			return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", optJob.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy công việc", null), HttpStatus.NOT_FOUND);
		}
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
	public ResponseEntity<Response> findByTags(String tag) {
		List<Job> jobs = jobRepo.findByTags(tag);
		if (jobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy công việc với tag này", null), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> updateJob(Job jobDetails) {
	    Optional<Job> optionalJob = jobRepo.findById(jobDetails.getId());

	    if (!optionalJob.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new Response(false, "Không tìm thấy công việc để cập nhật", null));
	    }

	    Job existingJob = optionalJob.get();
	    
	    existingJob.setName(jobDetails.getName());
	    existingJob.setJobtype(jobDetails.getJobtype());
	    existingJob.setSalary(jobDetails.getSalary());
	    existingJob.setTags(jobDetails.getTags());
	    existingJob.setDescription(jobDetails.getDescription());
	    existingJob.setRequiredskill(jobDetails.getRequiredskill());
	    existingJob.setActive(jobDetails.isActive());
	    existingJob.setBenefit(jobDetails.getBenefit());
	    existingJob.setField(jobDetails.getField());
	    existingJob.setQuantity(jobDetails.getQuantity());

	    try {
	        Job updatedJob = jobRepo.save(existingJob);
	        return ResponseEntity.ok(new Response(true, "Cập nhật công việc thành công", updatedJob));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new Response(false, "Cập nhật công việc thất bại", null));
	    }
	}

	@Override
	public ResponseEntity<Response> findAll(Specification<Job> spec) {
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobRepo.findAll(spec)), HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Response> findNewJobs() {
		List<Job> newjobs = jobRepo.findAll();
		if (newjobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Danh sách công việc trống", null), HttpStatus.OK);
		}
		if (newjobs.size() > 3) {
			newjobs = newjobs.subList(0, 3);
		}
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", newjobs), HttpStatus.OK);
	}
}

