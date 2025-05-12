package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.service.JobService;

import java.util.Optional;

@RestController
@RequestMapping("/employer/job")
public class JobManageAPI {

	@Autowired
	JobService jobServ;
	
	@GetMapping
	public ResponseEntity<Response> getAllJobs(){
		return jobServ.findAll();
	}

    @GetMapping("/by-employer")
    public ResponseEntity<Response> getJobsByEmail(@RequestParam String email){
        return jobServ.findByEmployerEmail(email);
    }

	@PostMapping("/add")
    public ResponseEntity<Response> createJob(@RequestBody String email, @RequestBody Job job) {
        job.setEmployer(null);
        return jobServ.save(job);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Response> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        jobDetails.setId(id);
        return jobServ.updateJob(jobDetails);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Response> toggleJobStatus(@PathVariable Long id) {
        return jobServ.toggleJobStatus(id);
    }
}
