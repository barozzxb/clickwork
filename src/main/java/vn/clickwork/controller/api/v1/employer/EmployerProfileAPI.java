package vn.clickwork.controller.api.v1.employer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.EmployerProfileDTO;
import vn.clickwork.service.EmployerService;
import vn.clickwork.service.NotificationService;

@RestController
@RequestMapping("/api/employer/profile")
public class EmployerProfileAPI {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private NotificationService notificationService;

    // Lấy thông tin profile
    @GetMapping
    public ResponseEntity<Response> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(employerService.getProfile(username));
    }

    // Cập nhật profile (không cho đổi email, username)
    @PutMapping
    public ResponseEntity<Response> updateProfile(
            Authentication authentication,
            @RequestBody EmployerProfileDTO dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(employerService.updateProfile(username, dto));
    }

    // Lấy tất cả thông báo của employer
    @GetMapping("/notifications")
    public ResponseEntity<Response> getNotifications(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getEmployerNotifications(username));
    }
}
