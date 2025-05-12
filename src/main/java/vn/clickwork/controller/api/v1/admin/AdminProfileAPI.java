package vn.clickwork.controller.api.v1.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.model.Response;
import vn.clickwork.model.dto.AdminProfileDTO;
import vn.clickwork.model.dto.NotificationDTO;
import vn.clickwork.model.request.AddressRequest;
import vn.clickwork.model.request.PasswordChangeRequest;
import vn.clickwork.service.AdminProfileService;
import vn.clickwork.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profile")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProfileAPI {

    @Autowired
    private AdminProfileService adminProfileService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Response> getAdminProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AdminProfileDTO profile = adminProfileService.getAdminProfile(username);
            if (profile == null) {
                return ResponseEntity.badRequest().body(new Response(false, "Admin profile not found", null));
            }

            return ResponseEntity.ok(new Response(true, "Admin profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateProfile(
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam("fullname") String fullname,
            @RequestParam(value = "phonenum", required = false) String phonenum) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AdminProfileDTO updatedProfile = adminProfileService.updateProfile(username, fullname, phonenum, avatarFile);
            return ResponseEntity.ok(new Response(true, "Profile updated successfully", updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to update profile: " + e.getMessage(), null));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Response> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean success = adminProfileService.changePassword(username, request);
            return ResponseEntity.ok(new Response(true, "Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to change password: " + e.getMessage(), null));
        }
    }

    @GetMapping("/addresses")
    public ResponseEntity<Response> getAddresses() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<AdminProfileDTO.AddressDTO> addresses = adminProfileService.getAddresses(username);
            return ResponseEntity.ok(new Response(true, "Addresses retrieved successfully", addresses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, e.getMessage(), null));
        }
    }

    @PostMapping("/address")
    public ResponseEntity<Response> addAddress(@RequestBody AddressRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AdminProfileDTO.AddressDTO address = adminProfileService.addAddress(username, request);
            return ResponseEntity.ok(new Response(true, "Address added successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to add address: " + e.getMessage(), null));
        }
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<Response> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AdminProfileDTO.AddressDTO address = adminProfileService.updateAddress(username, id, request);
            return ResponseEntity.ok(new Response(true, "Address updated successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to update address: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<Response> deleteAddress(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean success = adminProfileService.deleteAddress(username, id);
            return ResponseEntity.ok(new Response(true, "Address deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to delete address: " + e.getMessage(), null));
        }
    }

    // Notification endpoints

    @GetMapping("/notifications")
    public ResponseEntity<Response> getNotifications() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<NotificationDTO> notifications = notificationService.getAdminNotifications(username);
            return ResponseEntity.ok(new Response(true, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to retrieve notifications: " + e.getMessage(), null));
        }
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Response> getUnreadNotificationCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            long count = notificationService.getUnreadNotificationCount(username);
            return ResponseEntity.ok(new Response(true, "Unread notification count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to retrieve unread notification count: " + e.getMessage(), null));
        }
    }

    @PutMapping("/notifications/{notificationId}/mark-read")
    public ResponseEntity<Response> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            notificationService.markNotificationAsRead(username, notificationId);
            return ResponseEntity.ok(new Response(true, "Notification marked as read successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to mark notification as read: " + e.getMessage(), null));
        }
    }

    @PutMapping("/notifications/mark-all-read")
    public ResponseEntity<Response> markAllNotificationsAsRead() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            notificationService.markAllNotificationsAsRead(username);
            return ResponseEntity.ok(new Response(true, "All notifications marked as read successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(false, "Failed to mark all notifications as read: " + e.getMessage(), null));
        }
    }
}
