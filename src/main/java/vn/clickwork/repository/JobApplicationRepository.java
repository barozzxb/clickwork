package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}