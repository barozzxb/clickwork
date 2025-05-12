package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Job;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.JobService;

@RestController
@RequestMapping("api/employer/job")
@PreAuthorize("hasRole('EMPLOYER')")
public class JobManageAPI {

    @Autowired
    JobService jobServ;

    @Autowired
    EmployerRepository employerRepository;

    @GetMapping
    public ResponseEntity<Response> getAllJobs() {
        return jobServ.findAll();
    }

    @GetMapping("/by-employer")
    public ResponseEntity<Response> getJobsByEmail(@RequestParam String email) {
        return jobServ.findByEmployerEmail(email);
    }

    @PostMapping("/add")
    public ResponseEntity<Response> createJob(@RequestBody JobCreateRequest request) {
        // Kiểm tra dữ liệu đầu vào
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Email không được để trống", null));
        }
        if (request.getJob() == null) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Thông tin công việc không được để trống", null));
        }

        // Tìm Employer theo email
        Employer employer = employerRepository.findByEmail(request.getEmail());
        if (employer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Employer với email: " + request.getEmail(), null));
        }

        // Gán Employer cho Job
        Job job = request.getJob();
        job.setEmployer(employer);

        // Lưu Job
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

    @PostMapping("/filter")
    public ResponseEntity<Response> filterJobs(@RequestBody JobFilterRequest request) {
        // Employer có thể lọc cả công việc active và inactive
        return jobServ.filterJobs(request);
    }

    // DTO để nhận dữ liệu từ request
    public static class JobCreateRequest {
        private String email;
        private Job job;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Job getJob() {
            return job;
        }

        public void setJob(Job job) {
            this.job = job;
        }
    }
}
