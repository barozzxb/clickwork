package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Job;
import vn.clickwork.entity.SaveJob;

import java.util.List;

public interface SavedJobRepository extends JpaRepository<SaveJob, Long> {
    List<SaveJob> findByApplicant(Applicant applicant);
    void deleteByApplicantAndJob(Applicant applicant, Job job);
}
