package vn.clickwork.controller.api.v1.applicant;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import vn.clickwork.dto.JobApplicationResponseDTO;
import vn.clickwork.service.JobApplicationService;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping("/history/{username}")
    public List<JobApplicationResponseDTO> getApplicationHistory(@PathVariable String username) {
        return jobApplicationService.getApplicationsByApplicant(username);
    }
}
