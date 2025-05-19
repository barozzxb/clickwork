package vn.clickwork.service;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SavedJobDTO;
import vn.clickwork.entity.SaveJob;

import java.util.List;

public interface SavedJobService {
    Response saveJob(String applicantUsername, Long jobId);
    Response deleteSavedJob(String applicantUsername, Long jobId);
    List<SavedJobDTO> getSavedJobs(String applicantUsername);
    boolean isJobSaved(String username, Long jobId); // Phương thức mới
}
