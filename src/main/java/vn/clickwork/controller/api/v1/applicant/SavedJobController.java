package vn.clickwork.controller.api.v1.applicant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.entity.SaveJob;
import vn.clickwork.model.Response;
import vn.clickwork.service.SavedJobService;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @PostMapping("/save")
    public Response saveJob(@RequestParam String applicantUsername, @RequestParam Long jobId) {
        return savedJobService.saveJob(applicantUsername, jobId);
    }

    @DeleteMapping("/delete")
    public Response deleteSavedJob(@RequestParam String applicantUsername, @RequestParam Long jobId) {
        return savedJobService.deleteSavedJob(applicantUsername, jobId);
    }

    @GetMapping
    public List<SaveJob> getSavedJobs(@RequestParam String applicantUsername) {
        return savedJobService.getSavedJobs(applicantUsername);
    }
}
