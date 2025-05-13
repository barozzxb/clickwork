package vn.clickwork.service;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.JobApplicationResponseDTO;

import java.util.List;

public interface JobApplicationService {
    Response applyJob(String applicantUsername, Long jobId);
    boolean isJobApplied(String applicantUsername, Long jobId);
    List<JobApplicationResponseDTO> getApplicationsByApplicant(String username);
}