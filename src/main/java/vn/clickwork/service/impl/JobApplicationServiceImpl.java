package vn.clickwork.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.clickwork.dto.JobApplicationResponseDTO;
import vn.clickwork.entity.JobApplication;
import vn.clickwork.repository.JobApplicationRepository;
import vn.clickwork.service.JobApplicationService;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public List<JobApplicationResponseDTO> getApplicationsByApplicant(String username) {
        List<JobApplication> applications = jobApplicationRepository.findByApplicantUsername(username);
        return applications.stream()
                .map(ja -> {
                    JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
                    dto.setJobTitle(ja.getJob().getName());
                    dto.setTime(ja.getTime());
                    dto.setStatus(ja.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
