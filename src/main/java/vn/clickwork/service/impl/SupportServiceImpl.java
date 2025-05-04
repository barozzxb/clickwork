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
