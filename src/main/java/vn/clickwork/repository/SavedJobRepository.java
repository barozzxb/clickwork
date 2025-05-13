package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SaveJob, Long> {
	boolean existsByApplicantAccountUsernameAndJobId(String username, Long jobId);
    List<SaveJob> findByApplicant(Applicant applicant);
    void deleteByApplicantAndJob(Applicant applicant, Job job);
    Optional<SaveJob> findByApplicantAccountUsernameAndJobId(String username, Long jobId);
}