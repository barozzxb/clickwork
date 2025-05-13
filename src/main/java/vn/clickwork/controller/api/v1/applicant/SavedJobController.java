package vn.clickwork.controller.api.v1.applicant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.entity.SaveJob;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.ApplicationRequest;
import vn.clickwork.service.SavedJobService;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @PostMapping("/save")
    public Response saveJob(@RequestBody ApplicationRequest request) {
        return savedJobService.saveJob(request.getUsername(), request.getId());
    }

    @DeleteMapping("/delete")
    public Response deleteSavedJob(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam Long jobId) {
        String username = userDetails.getUsername(); // Lấy từ JWT
        return savedJobService.deleteSavedJob(username, jobId);
    }

    @GetMapping("/check")
    public Response checkSavedJob(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam Long jobId) {
        String username = userDetails.getUsername();
        boolean isSaved = savedJobService.isJobSaved(username, jobId);
        if (isSaved) {
            return new Response(false, "Công việc đã được lưu", null);
        }
        return new Response(true, "Công việc chưa được lưu", null);
    }
    @GetMapping
    public List<SaveJob> getSavedJobs(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername(); // Lấy từ JWT
        return savedJobService.getSavedJobs(username);
    }
}
