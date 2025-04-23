package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.service.JobService;

@RestController
@RequestMapping("/employer/job")
public class JobManageAPI {

	@Autowired
	JobService jobServ;
	
	@GetMapping
	public ResponseEntity<Response> getAll(){
		return jobServ.findAll();
	}
	
	@PostMapping("/add")
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        return jobServ.save(job);
    }

    @PutMapping("/edit?id={id}")
    public ResponseEntity<?> editJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        return jobServ.save(jobDetails);
    }

//    @PutMapping("/{id}/lock")
//    public ResponseEntity<?> setLockJob(@PathVariable Long id, @RequestParam boolean locked) {
//        Optional<Job> optJob = jobServ.findById(id);
//        if (optJob.isPresent()) {
//            Job job = optJob.get();
//            job.setActive(false);
//            Job updatedJob = jobServ.save(job);
//            return new ResponseEntity<Response>(new Response(true, "Cập nhật trạng thái khóa công việc thành công", updatedJob), HttpStatus.OK);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Không tìm thấy công việc");
//        }
//    }
}
