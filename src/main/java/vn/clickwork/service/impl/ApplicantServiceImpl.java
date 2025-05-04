package vn.clickwork.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.dto.SupportRequestDTO;
import vn.clickwork.entity.Account;
import vn.clickwork.entity.Admin;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Support;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.ApplicantDetailRequest;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.SupportRepository;
import vn.clickwork.service.AdminService;
import vn.clickwork.service.ApplicantService;
import vn.clickwork.service.EmployerService;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    @Autowired
    private ApplicantRepository applicantRepo;
    
    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private EmployerService employerService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SupportRepository supportRepository;

    @Override
    public ResponseEntity<Response> save(Applicant entity) {
        try {
            applicantRepo.save(entity);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật thông tin thành công", entity), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Response>(new Response(false, "Cập nhật thông tin thất bại", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Response> findAll() {
        List<Applicant> applicants = applicantRepo.findAll();
        if (applicants.isEmpty()) {
            return new ResponseEntity<Response>(new Response(true, "Danh sách trống", null), HttpStatus.OK);
        }
        return new ResponseEntity<Response>(new Response(true, "Lấy danh sách thành công", applicants), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response> findById(Long id) {
        Optional<Applicant> applicant = applicantRepo.findById(id);
        if (applicant.isPresent()) {
            return new ResponseEntity<Response>(new Response(true, "Lấy thông tin thành công", applicant.get()), HttpStatus.OK);
        }
        return new ResponseEntity<Response>(new Response(false, "Không tìm thấy thông tin", null), HttpStatus.NOT_FOUND);
    }

    @Override
    public long count() {
        return applicantRepo.count();
    }

    @Override
    public ResponseEntity<Response> update(ApplicantDetailRequest applicant) {
        try {
            Optional<Applicant> optional = applicantRepo.findById(applicant.getId());
            if (!optional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy thông tin", null));
            }

            Applicant existing = optional.get();

            // Cập nhật thông tin
            existing.setFullname(applicant.getFullname());
            existing.setDob(applicant.getDob());
            existing.setGender(applicant.getGender());
            existing.setInterested(applicant.getInterested());
            existing.setPhonenum(applicant.getPhonenum());
            existing.setEmail(applicant.getEmail());

            applicantRepo.save(existing);
            return ResponseEntity.ok(new Response(true, "Cập nhật thành công", existing));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, "Cập nhật thất bại", null));
        }
    }

    @Override
    public ResponseEntity<Response> findByUsername(String username) {
        Optional<Account> acc = accountRepo.findByUsername(username);
        if (!acc.isPresent()) {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy thông tin", null), HttpStatus.NOT_FOUND);
        }
        Applicant applicant = applicantRepo.findByAccount(acc.get());
        if (applicant != null) {
            return new ResponseEntity<Response>(new Response(true, "Lấy thông tin thành công", applicant), HttpStatus.OK);
        } else {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy thông tin", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response> findByEmail(String email) {
        Applicant applicant = applicantRepo.findByEmail(email);
        if (applicant != null) {
            return new ResponseEntity<Response>(new Response(true, "Lấy thông tin thành công", applicant), HttpStatus.OK);
        } else {
            return new ResponseEntity<Response>(new Response(false, "Không tìm thấy thông tin", null), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response> updateAvatar(String username, MultipartFile file) {
        Optional<Account> accOpt = accountRepo.findByUsername(username);
        if (!accOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy người dùng", null));
        }

        Applicant applicant = applicantRepo.findByAccount(accOpt.get());

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/avatar");
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/avatar/" + fileName;
            applicant.setAvatar(fileUrl);
            applicantRepo.save(applicant);

            return ResponseEntity.ok(new Response(true, "Upload avatar thành công", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Lỗi khi upload ảnh", null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> requestSupport(SupportRequestDTO supportRequestDTO) {
        // Tìm ứng viên theo username
    	Applicant applicant = applicantRepo.findByAccount_Username(supportRequestDTO.getApplicantUsername());
        if (applicant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new Response(false, "Không tìm thấy ứng viên", null));
        }

        // Tìm employer và admin nếu cần thiết
        Employer employer = employerService.findByUsername(supportRequestDTO.getEmployerUsername());
        if (employer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new Response(false, "Không tìm thấy employer", null));
        }

        Admin admin = adminService.findByAccount_Username(supportRequestDTO.getAdminUsername());
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new Response(false, "Không tìm thấy admin", null));
        }

        // Tạo đối tượng Support
        Support support = new Support();
        support.setTitle(supportRequestDTO.getTitle());
        support.setContent(supportRequestDTO.getContent());
        support.setSendat(supportRequestDTO.getSendat());
        support.setStatus(supportRequestDTO.getStatus());
        support.setApplicant(applicant);
        support.setEmployer(employer);
        support.setAdmin(admin);

        // Lưu yêu cầu hỗ trợ vào cơ sở dữ liệu
        supportRepository.save(support);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new Response(true, "Yêu cầu hỗ trợ đã được tạo thành công", support));
    }
}
