package vn.clickwork.repository;

import java.util.List;

import vn.clickwork.entity.Job;
import vn.clickwork.model.request.JobFilterRequest;

public interface JobRepositoryCustom {
    List<Job> filterJobs(JobFilterRequest filter);
}