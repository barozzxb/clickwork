
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
import vn.clickwork.model.dto.JobDTO;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.repository.JobRepositoryCustom;
import vn.clickwork.service.JobService;

@Service
public class JobServiceImpl implements JobService{

	@Autowired
	JobRepository jobRepo;

	@Autowired
	JobRepositoryCustom jobRepoCustom;

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
		List<JobDTO> jobDTOs = jobs.stream().map(this::mapToDTO).toList();
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> findById(Long id) {
		Optional<Job> optJob = jobRepo.findById(id);
		if (optJob.isPresent()) {
			JobDTO jobDTO = mapToDTO(optJob.get());
			return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTO), HttpStatus.OK);
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
		List<JobDTO> jobDTOs = jobs.stream().map(this::mapToDTO).toList();
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
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
	public ResponseEntity<Response> findNewJobs() {
		List<Job> newjobs = jobRepo.findAll();
		if (newjobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Danh sách công việc trống", null), HttpStatus.OK);
		}
		List<JobDTO> jobDTOs = newjobs.stream().map(this::mapToDTO).toList();
		if (jobDTOs.size() > 3) {
			jobDTOs = jobDTOs.subList(0, 3);
		}
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> filterJobs(JobFilterRequest request) {

		List<Job> jobs = jobRepoCustom.filterJobs(request);
		if (jobs.isEmpty()) {
			return new ResponseEntity<Response>(new Response(true, "Không tìm thấy công việc phù hợp", null), HttpStatus.OK);
		}
		List<JobDTO> jobDTOs = jobs.stream().map(this::mapToDTO).toList();
		return new ResponseEntity<Response>(new Response(true, "Lấy dữ liệu thành công", jobDTOs), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> findByEmployerEmail(String email) {
		List<Job> jobs = jobRepo.findByEmployerEmail(email);
		if (jobs.isEmpty()) {
			return ResponseEntity.ok(new Response(true, "Không có công việc nào", List.of()));
		}
		List<JobDTO> dtos = jobs.stream().map(this::mapToDTO).toList();
		return ResponseEntity.ok(new Response(true, "Lấy danh sách công việc thành công", dtos));
	}


	@Override
	public ResponseEntity<Response> toggleJobStatus(Long id) {
		Optional<Job> optional = jobRepo.findById(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new Response(false, "Không tìm thấy công việc", null));
		}

		Job job = optional.get();
		job.setActive(!job.isActive());

		try {
			jobRepo.save(job);
			return ResponseEntity.ok(new Response(true, "Cập nhật trạng thái thành công", job.isActive()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Thất bại khi cập nhật trạng thái", null));
		}
	}


	private JobDTO mapToDTO(Job job) {
		JobDTO dto = new JobDTO();
		dto.setId(job.getId());
		dto.setName(job.getName());
		dto.setJobtype(job.getJobtype().getValue());
		dto.setCreatedat(job.getCreatedat());
		dto.setSalary(job.getSalary());
		dto.setTags(job.getTags());
		dto.setDescription(job.getDescription());
		dto.setRequiredskill(job.getRequiredskill());
		dto.setBenefit(job.getBenefit());
		dto.setField(job.getField());
		dto.setQuantity(job.getQuantity());
		dto.setActive(job.isActive());
		dto.setEmployer(job.getEmployer());
		return dto;
	}
}

