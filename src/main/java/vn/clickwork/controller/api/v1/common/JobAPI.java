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
	public ResponseEntity<?> listJobs(){
		return jobServ.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getJob(@PathVariable("id") Long id){
		return jobServ.findById(id);
	}
	
	@PostMapping("/filter")
	public ResponseEntity<Response> filterJobs(@RequestBody JobFilterRequest request){
		return jobServ.filterJobs(request);
	}
	
	@GetMapping("/newjobs")
	public ResponseEntity<Response> getNewJobs(){
		return jobServ.findNewJobs();
	}
	
	@GetMapping("/get-employers")
	public ResponseEntity<Response> getEmployers(){
		return empServ.findAll();
	}
}
