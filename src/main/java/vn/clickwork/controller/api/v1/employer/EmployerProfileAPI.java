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
    public ResponseEntity<EmployerProfileDTO> getProfile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(employerService.getProfile(username));
    }

    // Cập nhật profile (không cho đổi email, username)
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody EmployerProfileDTO dto, Authentication authentication) {
        String username = authentication.getName();
        employerService.updateProfile(username, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/address")
    public ResponseEntity<Response> addAddress(@RequestBody EmployerProfileDTO.AddressDTO addressDTO,
            Authentication authentication) {
        String username = authentication.getName();
        employerService.addAddress(username, addressDTO);
        return ResponseEntity.ok(new Response(true, "Thêm địa chỉ thành công", null));
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<Response> updateAddress(@PathVariable Long id,
            @RequestBody EmployerProfileDTO.AddressDTO addressDTO, Authentication authentication) {
        String username = authentication.getName();
        employerService.updateAddress(username, id, addressDTO);
        return ResponseEntity.ok(new Response(true, "Cập nhật địa chỉ thành công", null));
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<Response> deleteAddress(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        employerService.deleteAddress(username, id);
        return ResponseEntity.ok(new Response(true, "Xóa địa chỉ thành công", null));
    }

    // Lấy tất cả thông báo của employer
    @GetMapping("/notifications")
    public ResponseEntity<Response> getNotifications(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(notificationService.getEmployerNotifications(username));
    }

    // Đánh dấu 1 thông báo là đã đọc
    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Response> markNotificationAsRead(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        notificationService.markEmployerNotificationAsRead(username, id);
        return ResponseEntity.ok(new Response(true, "Đã đánh dấu là đã đọc", null));
    }
}
