package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.entity.Appointment;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.JobApplication;
import vn.clickwork.entity.CV;
import vn.clickwork.enumeration.EApplyStatus;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.ApplicantDTO;
import vn.clickwork.model.dto.JobApplicationDTO;
import vn.clickwork.model.dto.AppointmentDTO;
import vn.clickwork.model.request.AppointmentRequest;
import vn.clickwork.repository.AppointmentRepository;
import vn.clickwork.repository.JobApplicationRepository;
import vn.clickwork.repository.CVRepository;
import vn.clickwork.service.NotificationService;
import vn.clickwork.model.dto.CVDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employer/applicants")
@PreAuthorize("hasRole('EMPLOYER')")
public class ApplicantManageAPI {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CVRepository cvRepository;

    // 1. Lấy danh sách ứng viên theo jobId, lọc theo skill, status, ngày ứng tuyển
    @GetMapping("/by-job/{jobId}")
    public ResponseEntity<Response> getApplicantsByJob(
            @PathVariable Long jobId,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appliedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appliedTo) {
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);

        List<JobApplicationDTO> filtered = applications.stream()
                .filter(app -> {
                    boolean match = true;
                    if (skill != null && !skill.isBlank()) {
                        match = match && app.getJob().getRequiredskill() != null &&
                                app.getJob().getRequiredskill().toLowerCase().contains(skill.toLowerCase());
                    }
                    if (status != null && !status.isBlank()) {
                        match = match && app.getStatus().name().equalsIgnoreCase(status);
                    }
                    if (appliedFrom != null) {
                        match = match && !app.getAppliedAt().toLocalDateTime().toLocalDate().isBefore(appliedFrom);
                    }
                    if (appliedTo != null) {
                        match = match && !app.getAppliedAt().toLocalDateTime().toLocalDate().isAfter(appliedTo);
                    }
                    return match;
                })
                .map(this::mapToJobApplicationDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new Response(true, "Lấy danh sách ứng viên thành công", filtered));
    }

    // 2. Xem chi tiết ứng viên
    @GetMapping("/{applicationId}")
    public ResponseEntity<Response> getApplicantDetail(@PathVariable Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên ứng tuyển"));
        JobApplicationDTO dto = mapToJobApplicationDTO(application);
        return ResponseEntity.ok(new Response(true, "Lấy thông tin ứng viên thành công", dto));
    }

    // 3. Cập nhật trạng thái ứng tuyển (duyệt/từ chối)
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<Response> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam("status") String status // "ACCEPTED" hoặc "REJECTED"
    ) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên ứng tuyển"));

        EApplyStatus newStatus;
        try {
            newStatus = EApplyStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Trạng thái không hợp lệ", null));
        }

        application.setStatus(newStatus);
        jobApplicationRepository.save(application);

        // Gửi thông báo cho applicant
        String notiContent = newStatus == EApplyStatus.ACCEPTED
                ? "Bạn đã được duyệt vào vòng phỏng vấn cho công việc: " + application.getJob().getName()
                : "Bạn đã bị từ chối cho công việc: " + application.getJob().getName();
        notificationService.createNotificationForApplicant(
                application.getApplicant(), notiContent);

        JobApplicationDTO dto = mapToJobApplicationDTO(application);
        return ResponseEntity.ok(new Response(true, "Cập nhật trạng thái thành công", dto));
    }

    // 4. Đặt lịch hẹn phỏng vấn
    @PostMapping("/{applicationId}/appointment")
    public ResponseEntity<Response> createAppointment(
            @PathVariable Long applicationId,
            @RequestBody AppointmentRequest request) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ứng viên ứng tuyển"));

        if (application.getStatus() != EApplyStatus.ACCEPTED) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Chỉ có thể đặt lịch cho ứng viên đã được duyệt", null));
        }

        Appointment appointment = new Appointment();
        appointment.setApplicant(application.getApplicant());
        appointment.setEmployer(application.getJob().getEmployer());
        appointment.setJob(application.getJob());
        appointment.setTime(request.getTime());
        appointment.setPlace(request.getPlace());
        appointment.setWebsite(request.getWebsite());
        appointment.setCreatedat(new java.sql.Timestamp(System.currentTimeMillis()));
        appointmentRepository.save(appointment);

        // Đổi trạng thái ứng tuyển thành ACCEPTED (nếu chưa)
        application.setStatus(EApplyStatus.ACCEPTED);
        jobApplicationRepository.save(application);

        // Gửi thông báo cho applicant
        String notiContent = "Bạn đã được mời phỏng vấn cho công việc: " + application.getJob().getName()
                + ". Thời gian: " + request.getTime() + ", Địa điểm: " + request.getPlace();
        notificationService.createNotificationForApplicant(
                application.getApplicant(), notiContent);

        // Trả về thông tin lịch hẹn (DTO nếu muốn)
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setTime(appointment.getTime());
        appointmentDTO.setPlace(appointment.getPlace());
        appointmentDTO.setWebsite(appointment.getWebsite());
        appointmentDTO.setJobName(application.getJob().getName());
        appointmentDTO.setApplicantName(application.getApplicant().getFullname());

        return ResponseEntity.ok(new Response(true, "Đặt lịch hẹn thành công", appointmentDTO));
    }

    // Helper method để map entity sang DTO
    private JobApplicationDTO mapToJobApplicationDTO(JobApplication app) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(app.getId());
        dto.setStatus(app.getStatus().name());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setJobName(app.getJob().getName());
        dto.setJobId(app.getJob().getId());

        Applicant applicant = app.getApplicant();
        if (applicant != null) {
            ApplicantDTO applicantDTO = new ApplicantDTO();
            applicantDTO.setId(applicant.getId());
            applicantDTO.setFullname(applicant.getFullname());
            applicantDTO.setEmail(applicant.getEmail());
            applicantDTO.setPhone(applicant.getPhonenum());

            // Lấy CV mặc định
            CV defaultCV = null;
            if (applicant.getAccount() != null) {
                defaultCV = cvRepository
                        .findByApplicantAccountUsernameAndIsDefaultTrue(applicant.getAccount().getUsername());
            }
            if (defaultCV != null) {
                CVDTO cvdto = new CVDTO();
                cvdto.setId(defaultCV.getId());
                cvdto.setName(defaultCV.getName());
                cvdto.setFile(defaultCV.getFile());
                applicantDTO.setDefaultCV(cvdto);
            }
            dto.setApplicant(applicantDTO);
        }
        return dto;
    }
}
