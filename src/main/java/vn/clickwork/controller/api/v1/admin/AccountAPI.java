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
}