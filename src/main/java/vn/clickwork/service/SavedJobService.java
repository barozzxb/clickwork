package vn.clickwork.service;

import vn.clickwork.model.Response;
import vn.clickwork.entity.SaveJob;

import java.util.List;

public interface SavedJobService {
    Response saveJob(String applicantUsername, Long jobId);
    Response deleteSavedJob(String applicantUsername, Long jobId);
    List<SaveJob> getSavedJobs(String applicantUsername);
    boolean isJobSaved(String username, Long jobId); // Phương thức mới
}
