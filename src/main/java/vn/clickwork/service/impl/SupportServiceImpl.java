package vn.clickwork.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.dto.SupportRequestDTO;
import vn.clickwork.entity.Support;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.model.Response;
import vn.clickwork.repository.SupportRepository;
import vn.clickwork.service.SupportService;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.enumeration.EResponseStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Notification;
import vn.clickwork.entity.Support;
import vn.clickwork.entity.Admin;
import vn.clickwork.enumeration.ENotiType;
import vn.clickwork.enumeration.EResponseStatus;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SupportResponseDTO;
import vn.clickwork.model.request.SupportResponseRequest;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.repository.NotificationRepository;
import vn.clickwork.repository.SupportRepository;
import vn.clickwork.repository.AdminRepository;
import vn.clickwork.service.SupportService;


@Service
public class SupportServiceImpl implements SupportService {


    private final SupportRepository supportRepository;
    private final ApplicantRepository applicantRepository;
    private final EmployerRepository employerRepository;

    @Autowired
    public SupportServiceImpl(SupportRepository supportRepository,
                              ApplicantRepository applicantRepository,
                              EmployerRepository employerRepository) {
        this.supportRepository = supportRepository;
        this.applicantRepository = applicantRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    public Response createSupportRequest(SupportRequestDTO dto, String actorUsername) {
        // Tạo đối tượng yêu cầu hỗ trợ
        Support support = new Support();
        support.setTitle(dto.getTitle());
        support.setContent(dto.getContent());
        support.setSendat(Timestamp.from(Instant.now()));
        support.setStatus(EResponseStatus.PENDING);

        // Tìm người dùng theo username
        Optional<Applicant> applicantOpt = Optional.ofNullable(applicantRepository.findByAccount_Username(actorUsername));
        Optional<Employer> employerOpt = employerRepository.findByAccount_Username(actorUsername);

        if (applicantOpt.isPresent()) {
            support.setApplicant(applicantOpt.get());
        } else if (employerOpt.isPresent()) {
            support.setEmployer(employerOpt.get());
        } else {
            return new Response(false, "Người dùng không tồn tại!", null);
        }

        supportRepository.save(support);
        return new Response(true, "Yêu cầu hỗ trợ đã được gửi thành công!", null);
    }
}

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
    private AdminRepository adminRepository;

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
    public Response getAllSupportRequests(int page, int size, String search, String sortBy, String sortDir, String status) {
        try {
            logger.info("Fetching support requests: page={}, size={}, search='{}', sortBy={}, sortDir={}, status='{}'",
                    page, size, search, sortBy, sortDir, status);

            // Xử lý sort
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Xử lý tìm kiếm và lọc trạng thái
            Specification<Support> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // Lọc theo trạng thái
                if (status != null && !status.trim().isEmpty()) {
                    try {
                        EResponseStatus statusEnum = EResponseStatus.valueOf(status.trim());
                        predicates.add(cb.equal(root.get("status"), statusEnum));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid status value: {}", status);
                    }
                }

                // Tìm kiếm
                if (search != null && !search.trim().isEmpty()) {
                    String searchPattern = "%" + search.toLowerCase() + "%";
                    List<Predicate> searchPredicates = new ArrayList<>();

                    // Tìm kiếm theo title
                    searchPredicates.add(cb.like(cb.lower(root.get("title")), searchPattern));

                    // Tìm kiếm theo content
                    searchPredicates.add(cb.like(cb.lower(root.get("content")), searchPattern));

                    // Tìm kiếm theo applicant email
                    try {
                        searchPredicates.add(cb.like(cb.lower(root.join("applicant", JoinType.LEFT).get("email")), searchPattern));
                    } catch (Exception e) {
                        logger.warn("Error joining applicant: {}", e.getMessage());
                    }

                    // Tìm kiếm theo employer email
                    try {
                        searchPredicates.add(cb.like(cb.lower(root.join("employer", JoinType.LEFT).get("email")), searchPattern));
                    } catch (Exception e) {
                        logger.warn("Error joining employer: {}", e.getMessage());
                    }

                    // Tìm kiếm theo ID
                    try {
                        Long searchId = Long.parseLong(search.trim());
                        searchPredicates.add(cb.equal(root.get("id"), searchId));
                    } catch (NumberFormatException e) {
                        logger.debug("Search term '{}' is not a valid number, skipping ID search", search);
                    }

                    predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
                }

                logger.debug("Applying predicates: {}", predicates.size());
                return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
            };

            // Lấy danh sách phân trang
            Page<Support> supportPage = supportRepository.findAll(spec, pageable);
            logger.info("Found {} tickets on page {}, total pages: {}, total items: {}",
                    supportPage.getNumberOfElements(), supportPage.getNumber(),
                    supportPage.getTotalPages(), supportPage.getTotalElements());

            List<SupportResponseDTO> dtos = supportPage.getContent().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            // Chuẩn bị dữ liệu phản hồi
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tickets", dtos);
            responseData.put("totalPages", supportPage.getTotalPages());
            responseData.put("currentPage", supportPage.getNumber());
            responseData.put("totalItems", supportPage.getTotalElements());

            return new Response(true, "Lấy danh sách yêu cầu hỗ trợ thành công", responseData);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách yêu cầu hỗ trợ: {}", e.getMessage(), e);
            return new Response(false, "Không thể lấy danh sách yêu cầu hỗ trợ: " + e.getMessage(), null);
        }
    }

    @Override
    public Response getAllSupportRequests() {
        try {
            List<SupportResponseDTO> dtos = supportRepository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return new Response(true, "Lấy danh sách yêu cầu hỗ trợ thành công", dtos);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách yêu cầu hỗ trợ: {}", e.getMessage(), e);
            return new Response(false, "Không thể lấy danh sách yêu cầu hỗ trợ: " + e.getMessage(), null);
        }
    }

    @Override
    public Response getSupportRequestById(Long id) {
        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));
        return new Response(true, "Lấy chi tiết yêu cầu hỗ trợ thành công", mapToDTO(support));
    }

    @Override
    public Response respondToSupportRequest(Long id, SupportResponseRequest request) {
        if (request.getResponse() == null || request.getResponse().trim().isEmpty()) {
            return new Response(false, "Thông tin không hợp lệ, vui lòng nhập nội dung phản hồi", null);
        }
        if (request.getResponse().length() > 500) {
            return new Response(false, "Nội dung phản hồi không được vượt quá 500 ký tự", null);
        }

        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu hỗ trợ với ID: " + id));

        // Lấy thông tin admin từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.warn("Không tìm thấy admin với username: {}", username);
            return new Response(false, "Không thể xác định admin xử lý", null);
        }

        support.setResponse(request.getResponse());
        support.setStatus(EResponseStatus.RESPONDED);
        support.setSendat(new Timestamp(System.currentTimeMillis()));
        support.setAdmin(admin);
        supportRepository.save(support);

        String email = null;
        String usernameFromAccount = null;
        if (support.getApplicant() != null) {
            Applicant applicant = applicantRepository.findById(support.getApplicant().getId()).orElse(null);
            if (applicant != null) {
                email = applicant.getEmail();
                usernameFromAccount = applicant.getAccount() != null ? applicant.getAccount().getUsername() : "Người dùng";
            } else {
                logger.warn("Không tìm thấy Applicant với ID: {}", support.getApplicant().getId());
            }
        } else if (support.getEmployer() != null) {
            Employer employer = employerRepository.findById(support.getEmployer().getId()).orElse(null);
            if (employer != null) {
                email = employer.getEmail();
                usernameFromAccount = employer.getAccount() != null ? employer.getAccount().getUsername() : "Người dùng";
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
                            "<p>Yêu cầu hỗ trợ của bạn (ID: %d) đã được phản hồi bởi %s:</p>" +
                            "<p><strong>%s</strong></p>" +
                            "<p>Vui lòng đăng nhập vào hệ thống để xem chi tiết.</p>" +
                            "<p>Trân trọng,<br>Đội ngũ ClickWork</p>",
                    usernameFromAccount, id, username, request.getResponse()
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

        Notification notification = new Notification();
        notification.setTitle("Phản hồi yêu cầu hỗ trợ");
        notification.setContent(String.format("Yêu cầu hỗ trợ ID %d đã được phản hồi bởi %s: %s", id, username, request.getResponse()));
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

