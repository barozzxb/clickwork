package vn.clickwork.controller.api.v1.applicant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.JobApplicationResponseDTO;
import vn.clickwork.model.request.ApplicationRequest;
import vn.clickwork.service.JobApplicationService;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    @PostMapping("/apply")
    public Response applyJob(@RequestBody ApplicationRequest request) {
        return jobApplicationService.applyJob(request.getUsername(), request.getId());
    }

    @GetMapping("/check")
    public Response checkAppliedJob(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam Long jobId) {
        String username = userDetails.getUsername();
        boolean isApplied = jobApplicationService.isJobApplied(username, jobId);
        return new Response(!isApplied, isApplied ? "Công việc đã được ứng tuyển" : "Công việc chưa được ứng tuyển", null);
    }

    @GetMapping("/history/{username}")
    public List<JobApplicationResponseDTO> getApplicationHistory(@PathVariable String username) {
        return jobApplicationService.getApplicationsByApplicant(username);
    }
}