package vn.clickwork.controller.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.ReportRequestDTO;
import vn.clickwork.service.AccountService;
import vn.clickwork.util.JwtUtils;

@RestController
@RequestMapping("/api/report")
@PreAuthorize("hasAnyRole('APPLICANT', 'EMPLOYER')")
public class ReportController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<Response> createReport(
            @RequestBody ReportRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        // Kiểm tra header Authorization và trích xuất token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        // Xác thực token và lấy username từ token
        String username = jwtUtils.getUsernameFromJwtToken(token);

        // Gọi service để xử lý yêu cầu báo cáo
        Response response = accountService.createReport(dto, username);

        return ResponseEntity.ok(response);
    }
}
