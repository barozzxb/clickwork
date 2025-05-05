package vn.clickwork.controller.api.v1.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.clickwork.model.Response;
import vn.clickwork.service.DashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardAPI {

    private static final Logger logger = LoggerFactory.getLogger(DashboardAPI.class);

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getDashboardData() {
        try {
            logger.info("Yêu cầu lấy dữ liệu dashboard từ admin");
            Response response = dashboardService.getDashboardData();
            if (response.isStatus()) {
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Lấy dữ liệu dashboard thất bại: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý yêu cầu dashboard", e);
            Response errorResponse = new Response(false, "Lỗi server: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}