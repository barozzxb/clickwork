package vn.clickwork.service;


import org.springframework.http.ResponseEntity;

import vn.clickwork.entity.Account;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.ChangePasswordRequest;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;

import vn.clickwork.model.request.ReportResolveRequest;
import vn.clickwork.model.request.ResetPasswordRequest;

import java.util.List;
import java.util.Optional;

public interface AccountService {
	<S extends Account> S save(S entity);
	List<Account> findAll();
	Optional<Account> findById(String id);
	long count();
	void deleteById(String id);
	void delete(Account entity);
	Optional<Account> findByUsername(String username);
	Response login(LoginRequest loginModel);
	Response register(RegisterRequest model);
	ResponseEntity<Response> requestResetPassword(String email);
	ResponseEntity<Response> resetPassword(ResetPasswordRequest model);
	Response getAllAccounts(int page, int size, String search, String role, String status);
	Response getAccountByUsername(String username);
	Response suspendAccount(String username);
	Response unsuspendAccount(String username);
	Response deleteAccount(String username);
	Response getAllReports(int page, int size, String search, String status);
	Response getReportById(Long id);
	Response resolveReport(Long id, ReportResolveRequest request);
	Response updateAccount(String username, String role, String status);
	Response createAdminAccount(RegisterRequest model);
	Response getSystemEmails(String role, String search);
	Response sendAdminEmail(String subject, String message, List<String> emails, String role);

    ResponseEntity<Response> changePassword(ChangePasswordRequest request);
}