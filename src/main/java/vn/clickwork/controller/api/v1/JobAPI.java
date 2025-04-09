package vn.clickwork.controller.api.v1;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobAPI {

	@Autowired
	JobService jobServ;
	
	@GetMapping
	public ResponseEntity<?> listJobs(){
		return ResponseEntity.ok().body(jobServ.findAll());
	}
	
	@GetMapping("/id=?{id}")
	public ResponseEntity<?> getJob(@RequestParam("id") Long id){
		Optional<Job> optJob = jobServ.findById(id);
		if (optJob.isPresent()) {
			return new ResponseEntity<Response>(new Response(true, "Lấy dư liệu thành công", optJob.get()), HttpStatus.FOUND);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Không lấy được dữ liệu");
		}
	}
	

	@PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        Job createdJob = jobServ.save(job);
        return new ResponseEntity<Response>(new Response(true, "Tạo công việc thành công", createdJob), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        Optional<Job> optJob = jobServ.findById(id);
        if (optJob.isPresent()) {
            Job job = optJob.get();
            job.setName(jobDetails.getName());
            job.setDescription(jobDetails.getDescription());
            // Update other fields as necessary
            Job updatedJob = jobServ.save(job);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật công việc thành công", updatedJob), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy công việc");
        }
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<?> setLockJob(@PathVariable Long id, @RequestParam boolean locked) {
        Optional<Job> optJob = jobServ.findById(id);
        if (optJob.isPresent()) {
            Job job = optJob.get();
            job.setActive(false);
            Job updatedJob = jobServ.save(job);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật trạng thái khóa công việc thành công", updatedJob), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy công việc");
        }
    }
}
