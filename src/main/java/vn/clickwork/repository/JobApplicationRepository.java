package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.JobApplication;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
        boolean existsByApplicantAccountUsernameAndJobId(String username, Long jobId);

        List<JobApplication> findByApplicant(Applicant applicant);
        
        @Query("SELECT j FROM JobApplication j WHERE j.applicant.id = :applicantId")
        List<JobApplication> findByApplicantId(@Param("applicantId") Long applicantId);

        @Query("SELECT ja.status AS status, COUNT(ja) AS count " +
                        "FROM JobApplication ja " +
                        "GROUP BY ja.status")
        List<Object[]> countApplicationsByStatus();

        @Query(value = "SELECT DATE_FORMAT(ja.applied_at, '%Y-%m') AS month, COUNT(*) AS count " +
                        "FROM application ja " +
                        "GROUP BY DATE_FORMAT(ja.applied_at, '%Y-%m') " +
                        "ORDER BY month", nativeQuery = true)
        List<Object[]> countApplicationsByMonth();

        List<JobApplication> findByJobId(Long jobId);

}