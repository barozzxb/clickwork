package vn.clickwork.controller.api.v1.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.JobSummaryDTO;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.model.request.PageRequest;
import vn.clickwork.model.response.PageResponse;
import vn.clickwork.service.EmployerService;
import vn.clickwork.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobAPI {

	@Autowired
	JobService jobServ;

	@Autowired
	EmployerService empServ;

	@GetMapping
	public ResponseEntity<?> listJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
		ResponseEntity<Response> response = jobServ.findAllPaged(pageRequest);
		if (response.getBody().getBody() instanceof PageResponse) {
			PageResponse<JobSummaryDTO> pageResponse = (PageResponse<JobSummaryDTO>) response.getBody().getBody();
			return ResponseEntity.ok(new Response(true, "Lấy dữ liệu thành công", pageResponse));
		}
		return response;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getJob(@PathVariable("id") Long id) {
		return jobServ.findById(id);
	}

	@PostMapping("/filter")
	public ResponseEntity<Response> filterJobs(@RequestBody JobFilterRequest request) {
		// Chỉ trả về công việc active cho API công khai
		if (request.getIsActive() == null) {
			request.setIsActive(true);
		}
		return jobServ.filterJobs(request);
	}

	@GetMapping("/newjobs")
	public ResponseEntity<Response> getNewJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size) {
		PageRequest pageRequest = new PageRequest(page, size, "createdat", "desc");
		ResponseEntity<Response> response = jobServ.findAllPaged(pageRequest);
		if (response.getBody().getBody() instanceof PageResponse) {
			PageResponse<JobSummaryDTO> pageResponse = (PageResponse<JobSummaryDTO>) response.getBody().getBody();
			return ResponseEntity.ok(new Response(true, "Lấy dữ liệu thành công", pageResponse.getContent()));
		}
		return response;
	}

	@GetMapping("/get-employers")
	public ResponseEntity<Response> getEmployers() {
		return empServ.findAll();
	}

	@GetMapping("/tags/{tag}")
	public ResponseEntity<Response> getJobsByTag(@PathVariable String tag) {
		return jobServ.findByTags(tag);
	}

	@GetMapping("/search")
	public ResponseEntity<Response> searchJobs(@RequestParam(required = false) String keyword) {
		JobFilterRequest request = new JobFilterRequest();
		request.setName(keyword);
		request.setIsActive(true); // Chỉ trả về công việc active
		return jobServ.filterJobs(request);
	}

	@GetMapping("/salary-range")
	public ResponseEntity<Response> getJobsBySalaryRange(
			@RequestParam(required = false) Double min,
			@RequestParam(required = false) Double max) {
		JobFilterRequest request = new JobFilterRequest();
		request.setSalaryMin(min);
		request.setSalaryMax(max);
		request.setIsActive(true); // Chỉ trả về công việc active
		return jobServ.filterJobs(request);
	}

	@GetMapping("/by-type/{jobType}")
	public ResponseEntity<Response> getJobsByType(@PathVariable String jobType) {
		JobFilterRequest request = new JobFilterRequest();
		request.setJobType(jobType);
		request.setIsActive(true); // Chỉ trả về công việc active
		return jobServ.filterJobs(request);
	}

	@GetMapping("/by-tags")
	public ResponseEntity<Response> getJobsByTags(@RequestParam List<String> tags) {
		JobFilterRequest request = new JobFilterRequest();
		request.setTags(tags);
		request.setIsActive(true); // Chỉ trả về công việc active
		return jobServ.filterJobs(request);
	}

	@GetMapping("/by-employer/{employerId}")
	public ResponseEntity<Response> getJobsByEmployer(@PathVariable Long employerId) {
		JobFilterRequest request = new JobFilterRequest();
		request.setEmployerId(employerId);
		request.setIsActive(true); // Chỉ trả về công việc active
		return jobServ.filterJobs(request);
	}
}