package vn.clickwork.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.model.dto.*;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.JobApplicationRepository;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.repository.ReportRepository;
import vn.clickwork.service.ReportService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public StatisticsDTO getAllStatistics() {
        try {
            StatisticsDTO report = new StatisticsDTO();
            
            JobStatsDTO jobStats = getJobStatistics();
            logger.info("Job Stats: {}", jobStats);
            report.setJobStats(jobStats);
            
            ApplicationStatsDTO appStats = getApplicationStatistics();
            logger.info("Application Stats: {}", appStats);
            report.setApplicationStats(appStats);
            
            UserStatsDTO userStats = getUserStatistics();
            logger.info("User Stats: {}", userStats);
            report.setUserStats(userStats);
            
            List<Map<String, Object>> jobCats = getJobCategories();
            logger.info("Job Categories: {}", jobCats);
            report.setJobCategories(jobCats);
            
            ViolationStatsDTO violationStats = getViolationStatistics();
            logger.info("Violation Stats: {}", violationStats);
            report.setViolationStats(violationStats);

            return report;
        } catch (Exception e) {
            logger.error("Error retrieving statistics: {}", e.getMessage(), e);
            StatisticsDTO emptyReport = new StatisticsDTO();
            emptyReport.setApplicationStats(new ApplicationStatsDTO(0, Collections.emptyList(), Collections.emptyList()));
            emptyReport.setJobStats(new JobStatsDTO(0, 0, 0, Collections.emptyList(), Collections.emptyList()));
            return emptyReport;
        }
    }

    private JobStatsDTO getJobStatistics() {
        try {
            long totalJobs = jobRepository.count();
            long activeJobs = jobRepository.countByIsActiveTrue();
            long inactiveJobs = jobRepository.countByIsActiveFalse();

            List<Map<String, Object>> jobsByType = jobRepository.countJobsByType()
                    .stream()
                    .map(entry -> {
                        String type = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", type);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> jobsByMonth = jobRepository.countJobsByMonth()
                    .stream()
                    .map(entry -> {
                        String month = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", month);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            return new JobStatsDTO(totalJobs, activeJobs, inactiveJobs, jobsByType, jobsByMonth);
        } catch (Exception e) {
            logger.error("Error retrieving job statistics: {}", e.getMessage());
            return new JobStatsDTO(0, 0, 0, Collections.emptyList(), Collections.emptyList());
        }
    }

    private ApplicationStatsDTO getApplicationStatistics() {
        try {
            // Lấy tổng số applications
            long totalApplications = jobApplicationRepository.count();
            logger.info("Total applications: {}", totalApplications);

            // Lấy applications theo status
            List<Map<String, Object>> applicationsByStatus = jobApplicationRepository.countApplicationsByStatus()
                    .stream()
                    .map(entry -> {
                        String status = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", status);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());
            logger.info("Applications by status: {}", applicationsByStatus);

            // Lấy applications theo tháng
            List<Map<String, Object>> applicationsByMonth = jobApplicationRepository.countApplicationsByMonth()
                    .stream()
                    .map(entry -> {
                        String month = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", month);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());
            logger.info("Applications by month: {}", applicationsByMonth);

            // Kiểm tra null và trả về kết quả
            if (applicationsByMonth == null) {
                applicationsByMonth = Collections.emptyList();
            }
            if (applicationsByStatus == null) {
                applicationsByStatus = Collections.emptyList();
            }

            return new ApplicationStatsDTO(totalApplications, applicationsByStatus, applicationsByMonth);
        } catch (Exception e) {
            logger.error("Error retrieving application statistics: {}", e.getMessage(), e);
            return new ApplicationStatsDTO(0, Collections.emptyList(), Collections.emptyList());
        }
    }

    private UserStatsDTO getUserStatistics() {
        try {
            long totalUsers = accountRepository.count();

            List<Map<String, Object>> usersByRole = accountRepository.countAccountsByRole()
                    .stream()
                    .map(entry -> {
                        String role = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", role);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> usersByStatus = accountRepository.countAccountsByStatus()
                    .stream()
                    .map(entry -> {
                        String status = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", status);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> registrationsByMonth = accountRepository.countAccountsByMonth()
                    .stream()
                    .map(entry -> {
                        String month = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", month);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            return new UserStatsDTO(totalUsers, usersByRole, usersByStatus, registrationsByMonth);
        } catch (Exception e) {
            logger.error("Error retrieving user statistics: {}", e.getMessage());
            return new UserStatsDTO(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }
    }

    private List<Map<String, Object>> getJobCategories() {
        try {
            long jobListings = jobRepository.count();
            return jobRepository.countJobsByField()
                    .stream()
                    .map(dto -> {
                        String field = dto.getField() != null ? dto.getField() : "Others";
                        long count = dto.getCount() != null ? dto.getCount() : 0L;
                        double percentage = (count * 100.0) / Math.max(jobListings, 1);

                        Map<String, Object> result = new HashMap<>();
                        result.put("name", field);
                        result.put("value", Math.round(percentage));
                        return result;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving job categories: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ViolationStatsDTO getViolationStatistics() {
        try {
            long totalViolations = reportRepository.count();

            List<Map<String, Object>> violationsByStatus = reportRepository.countReportsByStatus()
                    .stream()
                    .map(entry -> {
                        String status = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", status);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> violationsByMonth = reportRepository.countReportsByMonth()
                    .stream()
                    .map(entry -> {
                        String month = entry[0] != null ? entry[0].toString() : "Unknown";
                        long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;
                        Map<String, Object> result = new HashMap<>();
                        result.put("name", month);
                        result.put("count", count);
                        return result;
                    })
                    .collect(Collectors.toList());

            return new ViolationStatsDTO(totalViolations, violationsByStatus, violationsByMonth);
        } catch (Exception e) {
            logger.error("Error retrieving violation statistics: {}", e.getMessage());
            return new ViolationStatsDTO(0, Collections.emptyList(), Collections.emptyList());
        }
    }
}