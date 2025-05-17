package vn.clickwork.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.model.dto.SupportRequestDTO;
import vn.clickwork.model.Response;
import vn.clickwork.service.SupportService;
import vn.clickwork.util.JwtUtils;

@RestController
@RequestMapping("/api/support")
@PreAuthorize("hasAnyRole('APPLICANT', 'EMPLOYER')")
public class SupportRequestController {

    private final SupportService supportService;
    private final JwtUtils jwtUtils;

    public SupportRequestController(SupportService supportService, JwtUtils jwtUtils) {
        this.supportService = supportService;
        this.jwtUtils = jwtUtils;
    }

    // Endpoint để gửi yêu cầu hỗ trợ
    @PostMapping("/request")
    public ResponseEntity<Response> createSupportRequest(
            @RequestBody SupportRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        // Kiểm tra header Authorization và trích xuất token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        // Xác thực token và lấy username từ token
        String username = jwtUtils.getUsernameFromJwtToken(token);

        // Gọi service để xử lý yêu cầu
        Response response = supportService.createSupportRequest(dto, username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<Response> getMySupportRequests(
            @RequestHeader("Authorization") String authHeader) {

        // Kiểm tra header Authorization và trích xuất token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        // Xác thực token và lấy username từ token
        String username = jwtUtils.getUsernameFromJwtToken(token);

        // TODO: Implement this method in SupportService
        // Response response = supportService.getMySupportRequests(username);

        return ResponseEntity.ok(new Response(false, "Chức năng đang được phát triển", null));
    }
}
