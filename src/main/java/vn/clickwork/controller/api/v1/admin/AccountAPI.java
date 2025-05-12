package vn.clickwork.controller.api.v1.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.RegisterRequest;
import vn.clickwork.model.request.ReportResolveRequest;
import vn.clickwork.model.request.UpdateAccountRequest;
import vn.clickwork.service.AccountService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AccountAPI {

	@Autowired
	private AccountService accountService;

	@GetMapping
	public ResponseEntity<Response> getAllAccounts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "") String search,
			@RequestParam(defaultValue = "") String role,
			@RequestParam(defaultValue = "") String status) {
		return ResponseEntity.ok(accountService.getAllAccounts(page, size, search, role, status));
	}

	@GetMapping("/{username}")
	public ResponseEntity<Response> getAccountByUsername(@PathVariable String username) {
		return ResponseEntity.ok(accountService.getAccountByUsername(username));
	}

	@PostMapping("/{username}/suspend")
	public ResponseEntity<Response> suspendAccount(@PathVariable String username) {
		return ResponseEntity.ok(accountService.suspendAccount(username));
	}

	@PostMapping("/{username}/unsuspend")
	public ResponseEntity<Response> unsuspendAccount(@PathVariable String username) {
		return ResponseEntity.ok(accountService.unsuspendAccount(username));
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<Response> deleteAccount(@PathVariable String username) {
		return ResponseEntity.ok(accountService.deleteAccount(username));
	}

	@GetMapping("/reports")
	public ResponseEntity<Response> getAllReports(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "") String search,
			@RequestParam(defaultValue = "") String status) {
		return ResponseEntity.ok(accountService.getAllReports(page, size, search, status));
	}

	@GetMapping("/reports/{id}")
	public ResponseEntity<Response> getReportById(@PathVariable Long id) {
		return ResponseEntity.ok(accountService.getReportById(id));
	}

	@PostMapping("/reports/{id}/resolve")
	public ResponseEntity<Response> resolveReport(
			@PathVariable Long id,
			@RequestBody ReportResolveRequest request) {
		return ResponseEntity.ok(accountService.resolveReport(id, request));
	}

	@PatchMapping("/{username}")
	public ResponseEntity<Response> updateAccount(
			@PathVariable String username,
			@RequestBody UpdateAccountRequest request) {
		return ResponseEntity.ok(accountService.updateAccount(username, request.getRole(), request.getStatus()));
	}

	@PostMapping("/admin")
	public ResponseEntity<Response> createAdminAccount(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(accountService.createAdminAccount(request));
	}

	@GetMapping("/emails")
	public ResponseEntity<Response> getSystemEmails(
			@RequestParam(defaultValue = "") String group,
			@RequestParam(required = false) String search) {

		String role = mapGroupToRole(group); // ánh xạ frontend group -> ERole/INACTIVE/ALL
		return ResponseEntity.ok(accountService.getSystemEmails(role, search));
	}

	@PostMapping("/send-email")
	public ResponseEntity<Response> sendEmail(@RequestBody Map<String, Object> request) {
		try {
			String subject = (String) request.get("subject");
			String body = (String) request.get("body");
			String to = (String) request.get("to");
			String group = (String) request.get("group");
			List<String> emails = request.get("emails") != null ? (List<String>) request.get("emails") : null;

			if (subject == null || subject.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(new Response(false, "Subject is required", null));
			}
			if (body == null || body.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(new Response(false, "Body is required", null));
			}

			String role = group != null ? mapGroupToRole(group) : null;
			if (to != null && !to.trim().isEmpty()) {
				// Single email
				return ResponseEntity.ok(accountService.sendAdminEmail(subject, body, List.of(to), null));
			} else if (group != null || emails != null) {
				// Bulk email
				return ResponseEntity.ok(accountService.sendAdminEmail(subject, body, emails, role));
			} else {
				return ResponseEntity.badRequest().body(new Response(false, "Either 'to', 'group', or 'emails' must be provided", null));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(false, "Invalid request: " + e.getMessage(), null));
		}
	}

	private String mapGroupToRole(String group) {
		if (group == null || group.trim().isEmpty()) {
			return "ALL";
		}
		switch (group.toLowerCase()) {
			case "all":
				return "ALL";
			case "jobseekers":
				return "APPLICANT";
			case "employers":
				return "EMPLOYER";
			case "admins":
				return "ADMIN";
			case "inactive":
				return "INACTIVE";
			default:
				return group.toUpperCase(); // Fallback to direct role name
		}
	}
}