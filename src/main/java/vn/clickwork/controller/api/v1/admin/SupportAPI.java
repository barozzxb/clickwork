package vn.clickwork.controller.api.v1.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.SupportResponseRequest;
import vn.clickwork.service.SupportService;

@RestController
@RequestMapping("/api/support")
@PreAuthorize("hasRole('ADMIN')")
public class SupportAPI {

    @Autowired
    private SupportService supportService;

    @GetMapping
    public ResponseEntity<Response> getAllSupportRequests() {
        return ResponseEntity.ok(supportService.getAllSupportRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getSupportRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getSupportRequestById(id));
    }

    @PostMapping("/{id}/response")
    public ResponseEntity<Response> respondToSupportRequest(@PathVariable Long id, @RequestBody SupportResponseRequest request) {
        return ResponseEntity.ok(supportService.respondToSupportRequest(id, request));
    }
}