package vn.clickwork.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Notification;
import vn.clickwork.entity.Support;
import vn.clickwork.enumeration.ENotiType;
import vn.clickwork.enumeration.EResponseStatus;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SupportResponseDTO;
import vn.clickwork.model.request.SupportResponseRequest;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.repository.NotificationRepository;
import vn.clickwork.repository.SupportRepository;
import vn.clickwork.service.SupportService;

@Service
public class SupportServiceImpl implements SupportService {

    private static final Logger logger = LoggerFactory.getLogger(SupportServiceImpl.class);

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    private SupportResponseDTO mapToDTO(Support support) {
        SupportResponseDTO dto = new SupportResponseDTO();
        dto.setId(support.getId());
        dto.setTitle(support.getTitle());
        dto.setContent(support.getContent());
        dto.setSendat(support.getSendat());
        dto.setStatus(support.getStatus());
        dto.setResponse(support.getResponse());
        if (support.getApplicant() != null) {
            dto.setApplicantId(support.getApplicant().getId());
            dto.setApplicantEmail(support.getApplicant().getEmail());
        }
        if (support.getEmployer() != null) {
            dto.setEmployerId(support.getEmployer().getId());
            dto.setEmployerEmail(support.getEmployer().getEmail());
        }
        if (support.getAdmin() != null) {
            dto.setAdminUsername(support.getAdmin().getAccount().getUsername());
        }
        return dto;
    }

    @Override
    public Response getAllSupportRequests() {
        List<SupportResponseDTO> dtos = supportRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new Response(true, "Lấy danh sách yêu cầu hỗ trợ thành công", dtos);
    }

    @Override
    public Response getSupportRequestById(Long id) {
        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        return new Response(true, "Lấy chi tiết yêu cầu hỗ trợ thành công", mapToDTO(support));
    }

    @Override
    public Response respondToSupportRequest(Long id, SupportResponseRequest request) {
        // Kiểm tra định dạng phản hồi
        if (request.getResponse() == null || request.getResponse().trim().isEmpty()) {
            return new Response(false, "Thông tin không hợp lệ, vui lòng nhập nội dung phản hồi", null);
        }
        if (request.getResponse().length() > 500) {
            return new Response(false, "Nội dung phản hồi không được vượt quá 500 ký tự", null);
        }

        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));

        // Cập nhật phản hồi và trạng thái
        support.setResponse(request.getResponse());
        support.setStatus(EResponseStatus.RESPONDED);
        support.setSendat(new Timestamp(System.currentTimeMillis()));
        supportRepository.save(support);

        // Gửi email thông báo
        String email = null;
        String username = null;
        if (support.getApplicant() != null) {
            Applicant applicant = applicantRepository.findById(support.getApplicant().getId()).orElse(null);
            if (applicant != null) {
                email = applicant.getEmail();
                username = applicant.getAccount() != null ? applicant.getAccount().getUsername() : "Người dùng";
            } else {
                logger.warn("Không tìm thấy Applicant với ID: {}", support.getApplicant().getId());
            }
        } else if (support.getEmployer() != null) {
            Employer employer = employerRepository.findById(support.getEmployer().getId()).orElse(null);
            if (employer != null) {
                email = employer.getEmail();
                username = employer.getAccount() != null ? employer.getAccount().getUsername() : "Người dùng";
            } else {
                logger.warn("Không tìm thấy Employer với ID: {}", support.getEmployer().getId());
            }
        } else {
            logger.warn("Yêu cầu hỗ trợ ID: {} không có Applicant hoặc Employer", id);
        }

        String emailMessage = null;
        if (email != null && !email.trim().isEmpty()) {
            String emailContent = String.format(
                    "<h3>Phản hồi từ ClickWork</h3>" +
                            "<p>Chào %s,</p>" +
                            "<p>Yêu cầu hỗ trợ của bạn (ID: %d) đã được phản hồi:</p>" +
                            "<p><strong>%s</strong></p>" +
                            "<p>Vui lòng đăng nhập vào hệ thống để xem chi tiết.</p>" +
                            "<p>Trân trọng,<br>Đội ngũ ClickWork</p>",
                    username, id, request.getResponse()
            );
            try {
                logger.info("Gửi email đến: {} với chủ đề: Phản hồi yêu cầu hỗ trợ ID {}", email, id);
                emailService.sendEmail(email, "Phản hồi yêu cầu hỗ trợ", emailContent);
            } catch (RuntimeException e) {
                logger.error("Không thể gửi email đến {}: {}", email, e.getMessage(), e);
                emailMessage = "Phản hồi đã được gửi, nhưng không thể gửi email thông báo: " + e.getMessage();
            }
        } else {
            logger.warn("Không có email hợp lệ để gửi thông báo cho yêu cầu hỗ trợ ID: {}", id);
            emailMessage = "Phản hồi đã được gửi, nhưng không tìm thấy email hợp lệ để thông báo.";
        }

        // Lưu thông báo vào hệ thống
        Notification notification = new Notification();
        notification.setTitle("Phản hồi yêu cầu hỗ trợ");
        notification.setContent(String.format("Yêu cầu hỗ trợ ID %d đã được phản hồi: %s", id, request.getResponse()));
        notification.setType(ENotiType.RESPONSE);
        notification.setSendat(new Timestamp(System.currentTimeMillis()));
        if (support.getApplicant() != null) {
            notification.setApplicants(List.of(support.getApplicant()));
        } else if (support.getEmployer() != null) {
            notification.setEmployers(List.of(support.getEmployer()));
        }
        notificationRepository.save(notification);

        String message = "Phản hồi đã được gửi thành công";
        if (emailMessage != null) {
            message += ". " + emailMessage;
        }
        return new Response(true, message, mapToDTO(support));
    }
}