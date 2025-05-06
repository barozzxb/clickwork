package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import vn.clickwork.entity.Report;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
    long countByReportedapplicant_Account_Username(String username);

    long countByReportedemployer_Account_Username(String username);

    @Query("SELECT r.status AS status, COUNT(r) AS count FROM Report r GROUP BY r.status")
    List<Object[]> countReportsByStatus();

    @Query("SELECT FUNCTION('DATE_FORMAT', r.sendat, '%Y-%m') AS month, COUNT(r) AS count " +
            "FROM Report r GROUP BY FUNCTION('DATE_FORMAT', r.sendat, '%Y-%m')")
    List<Object[]> countReportsByMonth();
}