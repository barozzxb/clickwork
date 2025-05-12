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
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.service.EmployerService;
import vn.clickwork.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobAPI {

	@Autowired
	JobService jobServ;

	@Autowired
	EmployerService empServ;

	@GetMapping
	public ResponseEntity<?> listJobs() {
		return jobServ.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getJob(@PathVariable("id") Long id) {
		return jobServ.findById(id);
	}

	@PostMapping("/filter")
	public ResponseEntity<Response> filterJobs(@RequestBody JobFilterRequest request) {
		// Đảm bảo chỉ hiển thị các công việc đang active cho public API
		request.setIsActive(true);
		return jobServ.filterJobs(request);
	}

	@GetMapping("/newjobs")
	public ResponseEntity<Response> getNewJobs() {
		return jobServ.findNewJobs();
	}

	@GetMapping("/get-employers")
	public ResponseEntity<Response> getEmployers() {
		return empServ.findAll();
	}

	@GetMapping("/tags/{tag}")
	public ResponseEntity<Response> getJobsByTag(@PathVariable String tag) {
		return jobServ.findByTags(tag);
	}

	@GetMapping("/field/{field}")
	public ResponseEntity<Response> getJobsByField(@PathVariable String field) {
		JobFilterRequest request = new JobFilterRequest();
		request.setField(field);
		request.setIsActive(true);
		return jobServ.filterJobs(request);
	}

	@GetMapping("/type/{jobType}")
	public ResponseEntity<Response> getJobsByType(@PathVariable String jobType) {
		JobFilterRequest request = new JobFilterRequest();
		request.setJobType(jobType);
		request.setIsActive(true);
		return jobServ.filterJobs(request);
	}

	@GetMapping("/search")
	public ResponseEntity<Response> searchJobs(@RequestParam(required = false) String keyword) {
		JobFilterRequest request = new JobFilterRequest();
		request.setName(keyword);
		request.setIsActive(true);
		return jobServ.filterJobs(request);
	}

	@GetMapping("/salary-range")
	public ResponseEntity<Response> getJobsBySalaryRange(
			@RequestParam(required = false) Integer min,
			@RequestParam(required = false) Integer max) {
		JobFilterRequest request = new JobFilterRequest();
		request.setSalaryMin(min);
		request.setSalaryMax(max);
		request.setIsActive(true);
		return jobServ.filterJobs(request);
	}
}
