package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import vn.clickwork.entity.Address;
import vn.clickwork.entity.Employer;
import vn.clickwork.enumeration.EJobType;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.EmployerDTO;
import vn.clickwork.model.dto.JobDTO;
import vn.clickwork.model.request.JobFilterRequest;
import vn.clickwork.model.request.PageRequest;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.EmployerService;
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

    @Autowired
    EmployerService employerService;

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
    public ResponseEntity<Response> getJobsByEmployer(
            @RequestParam(required = false) String email,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Nếu không có email trong request param, lấy từ token
            if (email == null || email.isEmpty()) {
                if (userDetails == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new Response(false, "Vui lòng đăng nhập để xem danh sách công việc", null));
                }
                // Lấy username từ token
                String username = userDetails.getUsername();
                // Sử dụng service để lấy email
                email = employerService.getEmployerEmailByUsername(username);
            }

            // Gọi service để lấy danh sách job theo email
            return jobServ.findByEmployerEmail(email);
        } catch (UsernameNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Lỗi khi lấy danh sách công việc: " + e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Response> createJob(
            @RequestBody JobCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // Lấy username từ token
            String username = userDetails.getUsername();

            // Sử dụng EmployerService để lấy email
            String email = employerService.getEmployerEmailByUsername(username);

            // Tìm employer theo email
            Employer employer = employerRepository.findByEmail(email);
            if (employer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy Employer với email: " + email, null));
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
            job.setAddress(jobDTO.getAddress());
            job.setTags(jobDTO.getTags());
            job.setDescription(jobDTO.getDescription());
            job.setRequiredskill(jobDTO.getRequiredskill());
            job.setBenefit(jobDTO.getBenefit());
            job.setField(jobDTO.getField());
            job.setQuantity(jobDTO.getQuantity());
            job.setActive(true);
            job.setEmployer(employer);

            // Nếu địa chỉ không được cung cấp nhưng employer có địa chỉ, dùng địa chỉ chính
            // của employer
            if ((job.getAddress() == null || job.getAddress().isEmpty()) &&
                    employer.getAddresses() != null && !employer.getAddresses().isEmpty()) {
                Address mainAddress = employer.getAddresses().get(0);
                job.setAddress(getFullAddressString(mainAddress));
            }

            ResponseEntity<Response> response = jobServ.save(job);
            if (response.getStatusCode() == HttpStatus.OK) {
                JobDTO responseDTO = mapToDTO(job);
                return ResponseEntity.ok(new Response(true, "Tạo công việc thành công", responseDTO));
            }
            return response;
        } catch (UsernameNotFoundException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, e.getMessage(), null));
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
            job.setAddress(jobDTO.getAddress());
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
        private JobDTO job;

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
        dto.setAddress(job.getAddress());
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

    private String getFullAddressString(Address address) {
        if (address == null)
            return null;

        StringBuilder sb = new StringBuilder();
        if (address.getDetail() != null && !address.getDetail().isEmpty()) {
            sb.append(address.getDetail());
        }
        if (address.getVillage() != null && !address.getVillage().isEmpty()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(address.getVillage());
        }
        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(address.getDistrict());
        }
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(address.getProvince());
        }
        if (address.getNation() != null && !address.getNation().isEmpty()
                && !address.getNation().equalsIgnoreCase("Việt Nam")) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(address.getNation());
        }
        return sb.toString();
    }
}