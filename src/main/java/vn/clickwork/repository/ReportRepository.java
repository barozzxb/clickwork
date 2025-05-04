package vn.clickwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clickwork.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
