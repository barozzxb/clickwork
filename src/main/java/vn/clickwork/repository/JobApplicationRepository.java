package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clickwork.entity.JobApplication;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @Query("SELECT ja.status AS status, COUNT(ja) AS count FROM JobApplication ja GROUP BY ja.status")
    List<Object[]> countApplicationsByStatus();

    @Query("SELECT FUNCTION('DATE_FORMAT', ja.time, '%Y-%m') AS month, COUNT(ja) AS count " +
            "FROM JobApplication ja GROUP BY FUNCTION('DATE_FORMAT', ja.time, '%Y-%m')")
    List<Object[]> countApplicationsByMonth();
}