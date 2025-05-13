package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.JobApplication;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByApplicantAccountUsernameAndJobId(String username, Long jobId);
    List<JobApplication> findByApplicant(Applicant applicant);
}