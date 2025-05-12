package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.repository.SavedJobRepository;
import vn.clickwork.service.SavedJobService;
import vn.clickwork.model.Response;

import java.sql.Timestamp;
import java.util.List;

@Service
public class SavedJobServiceImpl implements SavedJobService {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Response saveJob(String applicantUsername, Long jobId) {
        Applicant applicant = applicantRepository.findByAccount_Username(applicantUsername);
        Job job = jobRepository.findById(jobId).orElse(null);

        if (applicant == null || job == null) {
            return new Response(false, "Applicant or job not found", null);
        }

        SaveJob saveJob = new SaveJob();
        saveJob.setApplicant(applicant);
        saveJob.setJob(job);
        saveJob.setSavedAt(new Timestamp(System.currentTimeMillis()));

        savedJobRepository.save(saveJob);

        return new Response(true, "Job saved successfully", saveJob);
    }

    @Override
    public Response deleteSavedJob(String applicantUsername, Long jobId) {
        Applicant applicant = applicantRepository.findByAccount_Username(applicantUsername);
        Job job = jobRepository.findById(jobId).orElse(null);

        if (applicant == null || job == null) {
            return new Response(false, "Applicant or job not found", null);
        }

        savedJobRepository.deleteByApplicantAndJob(applicant, job);

        return new Response(true, "Job deleted successfully", null);
    }

    @Override
    public List<SaveJob> getSavedJobs(String applicantUsername) {
        Applicant applicant = applicantRepository.findByAccount_Username(applicantUsername);

        if (applicant == null) {
            return null;
        }

        return savedJobRepository.findByApplicant(applicant);
    }
}