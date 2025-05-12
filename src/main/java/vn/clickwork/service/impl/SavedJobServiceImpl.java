package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.repository.SavedJobRepository;
import vn.clickwork.service.SavedJobService;
import vn.clickwork.model.Response;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SavedJobServiceImpl implements SavedJobService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Response saveJob(String applicantUsername, Long jobId) {
        try {
            if (savedJobRepository.existsByApplicantAccountUsernameAndJobId(applicantUsername, jobId)) {
                return new Response(false, "Công việc đã được lưu", null);
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
            SaveJob saveJob = new SaveJob();
            saveJob.setApplicant(applicant);
            saveJob.setJob(jobOpt.get());
            saveJob.setSavedAt(new Timestamp(System.currentTimeMillis()));
            savedJobRepository.save(saveJob);
            return new Response(true, "Lưu công việc thành công", null);
        } catch (Exception e) {
            return new Response(false, "Lỗi khi lưu: " + e.getMessage(), null);
        }
    }

    @Override
    public Response deleteSavedJob(String applicantUsername, Long jobId) {
        try {
            Optional<SaveJob> saveJobOpt = savedJobRepository.findByApplicantAccountUsernameAndJobId(applicantUsername, jobId);
            if (saveJobOpt.isPresent()) {
                savedJobRepository.delete(saveJobOpt.get());
                return new Response(true, "Xóa công việc thành công", null);
            }
            return new Response(false, "Không tìm thấy công việc đã lưu", null);
        } catch (Exception e) {
            return new Response(false, "Lỗi khi xóa: " + e.getMessage(), null);
        }
    }

    @Override
    public List<SaveJob> getSavedJobs(String applicantUsername) {
        try {
            Optional<Account> acc = accountRepo.findByUsername(applicantUsername);
            if (!acc.isPresent()) {
                return Collections.emptyList();
            }
            Applicant applicant = applicantRepository.findByAccount(acc.get());
            if (applicant == null) {
                return Collections.emptyList();
            }
            return savedJobRepository.findByApplicant(applicant);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isJobSaved(String applicantUsername, Long jobId) {
        try {
            return savedJobRepository.existsByApplicantAccountUsernameAndJobId(applicantUsername, jobId);
        } catch (Exception e) {
            return false;
        }
    }
}