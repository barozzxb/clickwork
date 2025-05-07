package vn.clickwork.controller.api.v1.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobAPI {

	@Autowired
	JobService jobServ;
	
	@GetMapping
	public ResponseEntity<?> listJobs(){
		return jobServ.findAll();
	}
	
	@GetMapping("/id={id}")
	public ResponseEntity<?> getJob(@PathVariable("id") Long id){
		return jobServ.findById(id);
	}
	
}
