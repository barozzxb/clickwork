package vn.clickwork.service;

import java.util.List;
import vn.clickwork.dto.JobApplicationResponseDTO;

public interface JobApplicationService {
    List<JobApplicationResponseDTO> getApplicationsByApplicant(String username);
}
