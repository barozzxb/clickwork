package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Job;
import vn.clickwork.entity.JobApplication;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.JobApplicationResponseDTO;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.JobApplicationRepository;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.service.JobApplicationService;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobapplicationRepository;

    @Override
    public Response applyJob(String applicantUsername, Long jobId) {
        try {
            if (jobapplicationRepository.existsByApplicantAccountUsernameAndJobId(applicantUsername, jobId)) {
                return new Response(false, "Bạn đã ứng tuyển công việc này", null);
            }
            Optional<Account> acc = accountRepo.findByUsername(applicantUsername);
            if (!acc.isPresent()) {
                return new Response(false, "Không tìm thấy người dùng", null);
            }
            Applicant applicant = applicantRepository.findByAccount(acc.get());
            if (applicant == null) {
                return new Response(false, "Không tìm thấy ứng viên", null);
            }
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (!jobOpt.isPresent()) {
                return new Response(false, "Không tìm thấy công việc", null);
            }
            JobApplication application = new JobApplication();
            application.setApplicant(applicant);
            application.setJob(jobOpt.get());
            application.setAppliedAt(new Timestamp(System.currentTimeMillis()));
            jobapplicationRepository.save(application);
            return new Response(true, "Ứng tuyển thành công", null);
        } catch (Exception e) {
            return new Response(false, "Lỗi khi ứng tuyển: " + e.getMessage(), null);
        }
    }

    @Override
    public boolean isJobApplied(String applicantUsername, Long jobId) {
        try {
            return jobapplicationRepository.existsByApplicantAccountUsernameAndJobId(applicantUsername, jobId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<JobApplicationResponseDTO> getApplicationsByApplicant(String username) {
        try {
            Optional<Account> acc = accountRepo.findByUsername(username);
            if (!acc.isPresent()) {
                return Collections.emptyList();
            }
            Applicant applicant = applicantRepository.findByAccount(acc.get());
            if (applicant == null) {
                return Collections.emptyList();
            }
            List<JobApplication> applications = jobapplicationRepository.findByApplicant(applicant);
            return applications.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private JobApplicationResponseDTO toDTO(JobApplication application) {
        JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
        dto.setApplicationId(application.getId());
        if (application.getJob() != null) {
            dto.setJobId(application.getJob().getId());
            dto.setJobTitle(application.getJob().getName());
        }
        dto.setAppliedAt(application.getAppliedAt());
        return dto;
    }
}