package vn.clickwork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.JobApplication;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Lấy danh sách ứng tuyển theo username của applicant (dựa vào liên kết Applicant -> Account)
    @Query("SELECT ja FROM JobApplication ja JOIN ja.applicant a JOIN a.account acc WHERE acc.username = :username")
    List<JobApplication> findByApplicantUsername(@Param("username") String username);

    @Query("SELECT ja.status AS status, COUNT(ja) AS count FROM JobApplication ja GROUP BY ja.status")
    List<Object[]> countApplicationsByStatus();

    @Query("SELECT FUNCTION('DATE_FORMAT', ja.time, '%Y-%m') AS month, COUNT(ja) AS count " +
            "FROM JobApplication ja GROUP BY FUNCTION('DATE_FORMAT', ja.time, '%Y-%m')")
    List<Object[]> countApplicationsByMonth();
}