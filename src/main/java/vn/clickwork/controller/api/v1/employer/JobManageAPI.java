package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.clickwork.entity.Employer;
import vn.clickwork.enumeration.EJobType;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.EmployerDTO;
import vn.clickwork.model.dto.JobDTO;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.model.request.PageRequest;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.JobService;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/employer/job")
@PreAuthorize("hasRole('EMPLOYER')")
public class JobManageAPI {

    private static final Logger log = Logger.getLogger(JobManageAPI.class.getName());

    @Autowired
    JobService jobServ;

    @Autowired
    EmployerRepository employerRepository;

    @GetMapping
    public ResponseEntity<Response> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDir);
        return jobServ.findAllPaged(pageRequest);
    }

    @GetMapping("/by-employer")
    public ResponseEntity<Response> getJobsByEmail(@RequestParam String email) {
        return jobServ.findByEmployerEmail(email);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('EMPLOYER') and #request.email == authentication.principal.username")
    public ResponseEntity<Response> createJob(@RequestBody JobCreateRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Email không được để trống", null));
        }
        if (request.getJob() == null) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Thông tin công việc không được để trống", null));
        }

        try {
            Employer employer = employerRepository.findByEmail(request.getEmail());
            if (employer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy Employer với email: " + request.getEmail(), null));
            }

            JobDTO jobDTO = request.getJob();
            vn.clickwork.entity.Job job = new vn.clickwork.entity.Job();
            job.setName(jobDTO.getName());
            try {
                job.setJobtype(jobDTO.getJobtype() != null ? EJobType.valueOf(jobDTO.getJobtype()) : null);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new Response(false, "Loại công việc không hợp lệ: " + jobDTO.getJobtype(), null));
            }
            job.setSalary(jobDTO.getSalary());
            job.setTags(jobDTO.getTags());
            job.setDescription(jobDTO.getDescription());
            job.setRequiredskill(jobDTO.getRequiredskill());
            job.setBenefit(jobDTO.getBenefit());
            job.setField(jobDTO.getField());
            job.setQuantity(jobDTO.getQuantity());
            job.setActive(true);
            job.setEmployer(employer);

            ResponseEntity<Response> response = jobServ.save(job);
            if (response.getStatusCode() == HttpStatus.OK) {
                JobDTO responseDTO = mapToDTO(job);
                return ResponseEntity.ok(new Response(true, "Tạo công việc thành công", responseDTO));
            }
            return response;
        } catch (Exception e) {
            log.severe("Lỗi khi tạo công việc: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Lỗi khi tạo công việc: " + e.getMessage(), null));
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Response> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        try {
            ResponseEntity<Response> response = jobServ.findById(id);
            if (response.getStatusCode() != HttpStatus.OK || !(response.getBody().getBody() instanceof JobDTO)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy công việc", null));
            }

            vn.clickwork.entity.Job job = new vn.clickwork.entity.Job();
            job.setId(id);
            job.setName(jobDTO.getName());
            try {
                job.setJobtype(jobDTO.getJobtype() != null ? EJobType.valueOf(jobDTO.getJobtype()) : null);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(new Response(false, "Loại công việc không hợp lệ: " + jobDTO.getJobtype(), null));
            }
            job.setSalary(jobDTO.getSalary());
            job.setTags(jobDTO.getTags());
            job.setDescription(jobDTO.getDescription());
            job.setRequiredskill(jobDTO.getRequiredskill());
            job.setBenefit(jobDTO.getBenefit());
            job.setField(jobDTO.getField());
            job.setQuantity(jobDTO.getQuantity());
            job.setActive(jobDTO.isActive());
            if (jobDTO.getEmployer() != null) {
                Employer employer = employerRepository.findByEmail(jobDTO.getEmployer().getEmail());
                if (employer == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response(false, "Không tìm thấy Employer", null));
                }
                job.setEmployer(employer);
            }

            ResponseEntity<Response> updateResponse = jobServ.updateJob(job);
            if (updateResponse.getStatusCode() == HttpStatus.OK) {
                JobDTO responseDTO = mapToDTO(job);
                return ResponseEntity.ok(new Response(true, "Cập nhật công việc thành công", responseDTO));
            }
            return updateResponse;
        } catch (Exception e) {
            log.severe("Lỗi khi cập nhật công việc: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Lỗi khi cập nhật công việc: " + e.getMessage(), null));
        }
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
        private JobDTO job;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public JobDTO getJob() {
            return job;
        }

        public void setJob(JobDTO job) {
            this.job = job;
        }
    }

    private JobDTO mapToDTO(vn.clickwork.entity.Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setJobtype(job.getJobtype() != null ? job.getJobtype().name() : null);
        dto.setCreatedat(job.getCreatedat());
        dto.setSalary(job.getSalary());
        dto.setTags(job.getTags());
        dto.setDescription(job.getDescription());
        dto.setRequiredskill(job.getRequiredskill());
        dto.setBenefit(job.getBenefit());
        dto.setField(job.getField());
        dto.setQuantity(job.getQuantity());
        dto.setActive(job.isActive());
        if (job.getEmployer() != null) {
            EmployerDTO employerDTO = new EmployerDTO();
            employerDTO.setId(job.getEmployer().getId());
            employerDTO.setEmail(job.getEmployer().getEmail());
            employerDTO.setFullname(job.getEmployer().getFullname());
            dto.setEmployer(employerDTO);
        }
        return dto;
    }
}