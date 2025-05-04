package vn.clickwork.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.enumeration.EAccountStatus;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.SupportResponseDTO;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.JobApplicationRepository;
import vn.clickwork.repository.JobRepository;
import vn.clickwork.repository.SupportRepository;
import vn.clickwork.service.DashboardService;
import vn.clickwork.service.SupportService;
import vn.clickwork.model.dto.JobFieldCountDTO;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private SupportService supportService;

    @Override
    public Response getDashboardData() {
        try {
            // Đếm các metrics cơ bản
            long activeUsers = accountRepository.countByStatus(EAccountStatus.ACTIVE);
            long jobListings = jobRepository.count();
            long applications = jobApplicationRepository.count();
            long supportTickets = supportRepository.count();

            logger.info("Metrics: activeUsers={}, jobListings={}, applications={}, supportTickets={}",
                    activeUsers, jobListings, applications, supportTickets);

            // Lấy recent support tickets
            List<SupportResponseDTO> recentTickets = getRecentSupportTickets();

            // Lấy job categories
            List<Map<String, Object>> jobCategories = getJobCategories(jobListings);

            // Lấy user registrations
            List<Map<String, Object>> userRegistrations = getUserRegistrations();

            // Gộp dữ liệu trả về
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("activeUsers", activeUsers);
            dashboardData.put("jobListings", jobListings);
            dashboardData.put("applications", applications);
            dashboardData.put("supportTickets", supportTickets);
            dashboardData.put("recentTickets", recentTickets);
            dashboardData.put("jobCategories", jobCategories);
            dashboardData.put("userRegistrations", userRegistrations);

            return new Response(true, "Lấy dữ liệu dashboard thành công", dashboardData);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy dữ liệu dashboard", e);
            return new Response(false, "Không thể lấy dữ liệu dashboard: " + e.getMessage(), null);
        }
    }

    private List<SupportResponseDTO> getRecentSupportTickets() {
        try {
            Response supportResponse = supportService.getAllSupportRequests();
            Object data = supportResponse.getBody();
            if (data instanceof List<?>) {
                return ((List<?>) data).stream()
                        .filter(item -> item instanceof SupportResponseDTO)
                        .map(item -> (SupportResponseDTO) item)
                        .limit(5)
                        .collect(Collectors.toList());
            }
            logger.warn("Dữ liệu support tickets không hợp lệ, trả về danh sách rỗng");
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Lỗi khi lấy recent support tickets", e);
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getJobCategories(long jobListings) {
        try {
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
            logger.error("Lỗi khi lấy job categories", e);
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> getUserRegistrations() {
        try {
            return accountRepository.countAccountsByMonth()
                    .stream()
                    .map(entry -> {
                        try {
                            String month = entry[0] != null ? entry[0].toString() : "Unknown";
                            Long count = entry[1] != null ? ((Number) entry[1]).longValue() : 0L;

                            Map<String, Object> result = new HashMap<>();
                            result.put("name", month);
                            result.put("count", count);
                            return result;
                        } catch (Exception e) {
                            logger.error("Lỗi khi xử lý dữ liệu tháng: {}", e.getMessage());
                            Map<String, Object> fallback = new HashMap<>();
                            fallback.put("name", "Error");
                            fallback.put("count", 0L);
                            return fallback;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Lỗi khi lấy user registrations: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}