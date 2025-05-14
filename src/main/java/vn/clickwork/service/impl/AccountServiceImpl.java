package vn.clickwork.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.regex.Pattern;

import vn.clickwork.entity.*;
import vn.clickwork.enumeration.EAccountStatus;
import vn.clickwork.enumeration.ENotiType;
import vn.clickwork.enumeration.EResponseStatus;
import vn.clickwork.enumeration.ERole;
import vn.clickwork.model.Response;
import vn.clickwork.model.dto.AccountDTO;
import vn.clickwork.model.dto.ReportDTO;
import vn.clickwork.model.dto.ReportRequestDTO;
import vn.clickwork.model.request.ChangePasswordRequest;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;

import vn.clickwork.model.request.ReportResolveRequest;
import vn.clickwork.model.request.ResetPasswordRequest;

import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.repository.AdminRepository;
import vn.clickwork.repository.ReportRepository;
import vn.clickwork.repository.NotificationRepository;
import vn.clickwork.service.AccountService;
import vn.clickwork.util.JwtUtils;
import vn.clickwork.util.PasswordUtil;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	@Autowired
	AccountRepository accRepo;

	@Autowired
	ApplicantRepository appRepo;

	@Autowired
	EmployerRepository empRepo;

	@Autowired
	AdminRepository adminRepo;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	PasswordUtil passwordUtil;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	EmailService emailService;

	public <S extends Account> S save(S entity) {
		return accRepo.save(entity);
	}

	@Override
	public List<Account> findAll() {
		return accRepo.findAll();
	}

	@Override
	public Optional<Account> findById(String id) {
		return accRepo.findById(id);
	}

	@Override
	public long count() {
		return accRepo.count();
	}

	@Override
	public void deleteById(String id) {
		accRepo.deleteById(id);
	}

	@Override
	public void delete(Account entity) {
		accRepo.delete(entity);
	}

	@Override
	public Optional<Account> findByUsername(String username) {
		return accRepo.findByUsername(username);
	}

	@Override
	public Response login(LoginRequest loginModel) {

		if (loginModel.getUsername() == null || loginModel.getPassword() == null) {
			return new Response(false, "Vui lòng nhập đầy đủ thông tin đăng nhập", null);
		}

		Optional<Account> optAcc = this.findByUsername(loginModel.getUsername());
		if (optAcc.isPresent()) {
			Account acc = optAcc.get();

			
			

			if (passwordUtil.verifyPassword(loginModel.getPassword(), acc.getPassword())) {
				
				if (acc.getStatus() == EAccountStatus.INACTIVE) {
					
					HashMap<String, Object> data = new HashMap<>();
					data.put("accStatus", acc.getStatus());
					
					return new Response(false, "Tài khoản chưa được kích hoạt, vui lòng kiểm tra email để kích hoạt tài khoản",
							data);
				}
				
				if (acc.getStatus() == EAccountStatus.SUSPENDED) {
					String suspendedUntilMessage = "";
					HashMap<String, Object> data = new HashMap<>();
					if (acc.getSuspendedUntil() != null) {
						suspendedUntilMessage = " đến " + acc.getSuspendedUntil().toString();
						data.put("accStatus", acc.getStatus());
					}
					return new Response(false, "Tài khoản của bạn đã bị khóa" + suspendedUntilMessage, data);
				}
				
				String token = jwtUtils.generateToken(acc.getUsername(), acc.getRole());
				Map<String, Object> data = new HashMap<>();
				data.put("token", token);
				data.put("status", acc.getStatus());
				return new Response(true, "Đăng nhập thành công", data);
			} else {
				HashMap<String, Object> data = new HashMap<>();
				data.put("accStatus", acc.getStatus());
				return new Response(false, "Sai thông tin đăng nhập", data);
			}
		}
		return new Response(false, "Tài khoản không tồn tại, vui lòng thử lại hoặc tạo tài khoản mới", null);
	}

	@Override
	public Response register(RegisterRequest model) {
		Optional<Account> optAcc = this.findByUsername(model.getUsername());
		if (optAcc.isPresent()) {
			return new Response(false, "Tài khoản với username tương ứng đã tồn tại, vui lòng chọn username khác",
					null);
		}

		if (isExistByEmail(model.getEmail())) {
			return new Response(false, "Email này đã được dùng để đăng ký một tài khoản khác, vui lòng chọn email khác",
					null);
		}

		String hashedPassword = passwordUtil.hashPassword(model.getPassword());
		Account acc = new Account(model.getUsername(), hashedPassword, model.getRole());
		acc.setStatus(EAccountStatus.INACTIVE);
		if (model.getRole() == ERole.APPLICANT) {
			Applicant applicant = new Applicant();
			applicant.setAccount(acc);
			applicant.setEmail(model.getEmail());
			String fileUrl = "/uploads/avatar/user_default.png";
			applicant.setAvatar(fileUrl);
			acc.setApplicant(applicant);
		} else if (model.getRole() == ERole.EMPLOYER) {
			Employer employer = new Employer();
			employer.setAccount(acc);
			employer.setEmail(model.getEmail());
			String fileUrl = "/uploads/avatar/user_default.png";
			employer.setAvatar(fileUrl);
			acc.setEmployer(employer);
		} else if (model.getRole() == ERole.ADMIN) {
			Admin admin = new Admin();
			admin.setAccount(acc);
			admin.setEmail(model.getEmail());
			String fileUrl = "/uploads/avatar/user_default.png";
			admin.setAvatar(fileUrl);
			acc.setAdmin(admin);
		}
		accRepo.save(acc);
		HashMap<String, Object> data = new HashMap<>();
		data.put("accStatus", acc.getStatus());
		return new Response(true, "Đăng ký thành công", data);
	}

	@Override
	public ResponseEntity<Response> requestResetPassword(String email) {
		if (email == null) {
			return new ResponseEntity<>(new Response(false, "Bạn phải nhập email để tiếp tục", null),
					HttpStatus.BAD_REQUEST);
		}

		Account acc = getAccountByEmail(email);
		if (acc == null) {
			return new ResponseEntity<>(new Response(false, "Email chưa được dùng để đăng ký. Vui lòng thử lại", null),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new Response(true, "Email hợp lệ", null), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Response> resetPassword(ResetPasswordRequest model) {
		Account acc = getAccountByEmail(model.getEmail());
		String newPassword = passwordUtil.hashPassword(model.getPassword());
		acc.setPassword(newPassword);
		accRepo.save(acc);
		return new ResponseEntity<>(new Response(true, "Mật khẩu mới đã được gửi đến email của bạn", null),
				HttpStatus.OK);
	}

	private boolean isExistByEmail(String email) {
		return appRepo.findByEmail(email) != null || empRepo.findByEmail(email) != null
				|| adminRepo.findByEmail(email) != null;
	}

	private Account getAccountByEmail(String email) {
		Applicant applicant = appRepo.findByEmail(email);
		if (applicant != null) {
			return applicant.getAccount();
		}
		Employer employer = empRepo.findByEmail(email);
		if (employer != null) {
			return employer.getAccount();
		}
		Admin admin = adminRepo.findByEmail(email);
		if (admin != null) {
			return admin.getAccount();
		}
		return null;
	}

	private AccountDTO mapToAccountDTO(Account account) {
		AccountDTO dto = new AccountDTO();
		dto.setUsername(account.getUsername());
		dto.setRole(account.getRole().name());
		dto.setStatus(account.getStatus().name());
		dto.setCreatedAt(account.getCreatedAt());
		long violationCount = 0;
		if (account.getApplicant() != null) {
			violationCount += reportRepository.countByReportedapplicant_Account_Username(account.getUsername());
			dto.setFullName(account.getApplicant().getFullname());
			dto.setEmail(account.getApplicant().getEmail());
			dto.setPhoneNum(account.getApplicant().getPhonenum());
			dto.setAvatar(account.getApplicant().getAvatar());
		} else if (account.getEmployer() != null) {
			violationCount += reportRepository.countByReportedemployer_Account_Username(account.getUsername());
			dto.setFullName(account.getEmployer().getFullname());
			dto.setEmail(account.getEmployer().getEmail());
			dto.setPhoneNum(account.getEmployer().getPhonenum());
			dto.setAvatar(account.getEmployer().getAvatar());
		} else if (account.getAdmin() != null) {
			dto.setFullName(account.getAdmin().getFullname());
			dto.setEmail(account.getAdmin().getEmail());
			dto.setPhoneNum(account.getAdmin().getPhonenum());
			dto.setAvatar(account.getAdmin().getAvatar());
		}
		dto.setViolationCount(violationCount);
		return dto;
	}

	private ReportDTO mapToReportDTO(Report report) {
		ReportDTO dto = new ReportDTO();
		dto.setId(report.getId());
		dto.setTitle(report.getTitle());
		dto.setContent(report.getContent());
		dto.setSendat(report.getSendat());
		dto.setStatus(report.getStatus().name());
		if (report.getApplicant() != null) {
			dto.setSenderName(report.getApplicant().getFullname());
			dto.setSenderEmail(report.getApplicant().getEmail());
		} else if (report.getEmployer() != null) {
			dto.setSenderName(report.getEmployer().getFullname());
			dto.setSenderEmail(report.getEmployer().getEmail());
		}
		if (report.getReportedapplicant() != null) {
			dto.setReportedName(report.getReportedapplicant().getFullname());
			dto.setReportedEmail(report.getReportedapplicant().getEmail());
		} else if (report.getReportedemployer() != null) {
			dto.setReportedName(report.getReportedemployer().getFullname());
			dto.setReportedEmail(report.getReportedemployer().getEmail());
		}
		return dto;
	}

	@Override
	public Response getAllAccounts(int page, int size, String search, String role, String status) {
		try {
			logger.info("Fetching accounts: page={}, size={}, search='{}', role='{}', status='{}'", page, size, search,
					role, status);

			Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
			Pageable pageable = PageRequest.of(page, size, sort);

			Specification<Account> spec = (root, query, cb) -> {
				List<Predicate> predicates = new ArrayList<>();

				if (search != null && !search.trim().isEmpty()) {
					String searchPattern = "%" + search.toLowerCase() + "%";
					List<Predicate> searchPredicates = new ArrayList<>();
					searchPredicates.add(cb.like(cb.lower(root.get("username")), searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(
									root.join("applicant", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("applicant", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("employer", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("employer", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("admin", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("admin", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
				}

				if (role != null && !role.trim().isEmpty()) {
					predicates.add(cb.equal(root.get("role"), role));
				}

				if (status != null && !status.trim().isEmpty()) {
					predicates.add(cb.equal(root.get("status"), status));
				}

				return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
			};

			Page<Account> accountPage = accRepo.findAll(spec, pageable);
			List<AccountDTO> dtos = accountPage.getContent().stream().map(this::mapToAccountDTO)
					.collect(Collectors.toList());

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("accounts", dtos);
			responseData.put("totalPages", accountPage.getTotalPages());
			responseData.put("currentPage", accountPage.getNumber());
			responseData.put("totalItems", accountPage.getTotalElements());

			return new Response(true, "Lấy danh sách tài khoản thành công", responseData);
		} catch (Exception e) {
			logger.error("Lỗi khi lấy danh sách tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể lấy danh sách tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response getAccountByUsername(String username) {
		try {
			Account account = accRepo.findById(username).orElseThrow(
					() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));
			return new Response(true, "Lấy chi tiết tài khoản thành công", mapToAccountDTO(account));
		} catch (Exception e) {
			logger.error("Lỗi khi lấy chi tiết tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể lấy chi tiết tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response suspendAccount(String username) {
		try {
			Account account = accRepo.findById(username).orElseThrow(
					() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));

			if (account.getStatus() == EAccountStatus.SUSPENDED) {
				return new Response(false, "Tài khoản đã bị khóa", null);
			}

			account.setStatus(EAccountStatus.SUSPENDED);
			account.setSuspendedUntil(new Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
			accRepo.save(account);

			String email = account.getApplicant() != null ? account.getApplicant().getEmail()
					: account.getEmployer() != null ? account.getEmployer().getEmail()
							: account.getAdmin() != null ? account.getAdmin().getEmail() : null;

			if (email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches()) {
				String emailContent = String.format("<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>"
						+ "  <meta charset=\"UTF-8\"/>" + "  <style>"
						+ "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }"
						+ "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }"
						+ "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }"
						+ "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }"
						+ "  </style>" + "</head>" + "<body>" + "  <div class=\"container\">"
						+ "    <div class=\"header\">" + "      <h2>ClickWork</h2>" + "    </div>"
						+ "    <div class=\"content\">" + "      <p>Chào %s,</p>"
						+ "      <p>Tài khoản của bạn (%s) đã bị khóa do vi phạm chính sách.</p>"
						+ "      <p>Thời gian khóa: 30 ngày, đến %s.</p>"
						+ "      <p>Nếu không có hành động khắc phục, tài khoản sẽ bị xóa sau thời gian này.</p>"
						+ "    </div>" + "    <div class=\"footer\">" + "      © 2025 ClickWork. All rights reserved."
						+ "    </div>" + "  </div>" + "</body>" + "</html>",
						account.getApplicant() != null ? account.getApplicant().getFullname()
								: account.getEmployer() != null ? account.getEmployer().getFullname()
										: account.getAdmin() != null ? account.getAdmin().getFullname() : "Người dùng",
						username, account.getSuspendedUntil().toString());
				try {
					emailService.sendEmail(email, "Thông báo khóa tài khoản", emailContent);
					logger.info("Gửi email khóa tài khoản đến: {}", email);
				} catch (Exception e) {
					logger.warn("Không thể gửi email khóa tài khoản đến {}: {}", email, e.getMessage());
				}
			} else {
				logger.warn("Email không hợp lệ hoặc không tồn tại cho tài khoản: {}", username);
			}

			return new Response(true, "Khóa tài khoản thành công", mapToAccountDTO(account));
		} catch (Exception e) {
			logger.error("Lỗi khi khóa tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể khóa tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response unsuspendAccount(String username) {
		try {
			Account account = accRepo.findById(username).orElseThrow(
					() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));

			if (account.getStatus() != EAccountStatus.SUSPENDED) {
				return new Response(false, "Tài khoản không ở trạng thái khóa", null);
			}

			account.setStatus(EAccountStatus.ACTIVE);
			account.setSuspendedUntil(null);
			accRepo.save(account);

			return new Response(true, "Mở khóa tài khoản thành công", mapToAccountDTO(account));
		} catch (Exception e) {
			logger.error("Lỗi khi mở khóa tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể mở khóa tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response deleteAccount(String username) {
		try {
			Account account = accRepo.findById(username).orElseThrow(
					() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));

			accRepo.delete(account);
			return new Response(true, "Xóa tài khoản thành công", null);
		} catch (Exception e) {
			logger.error("Lỗi khi xóa tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể xóa tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response getAllReports(int page, int size, String search, String status) {
		try {
			logger.info("Fetching reports: page={}, size={}, search='{}', status='{}'", page, size, search, status);

			Sort sort = Sort.by(Sort.Direction.DESC, "sendat");
			Pageable pageable = PageRequest.of(page, size, sort);

			Specification<Report> spec = (root, query, cb) -> {
				List<Predicate> predicates = new ArrayList<>();

				if (search != null && !search.trim().isEmpty()) {
					String searchPattern = "%" + search.toLowerCase() + "%";
					List<Predicate> searchPredicates = new ArrayList<>();
					searchPredicates.add(cb.like(cb.lower(root.get("title")), searchPattern));
					searchPredicates.add(cb.like(cb.lower(root.get("content")), searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(
									root.join("applicant", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("applicant", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("employer", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(
							cb.lower(root.join("employer", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					searchPredicates.add(cb.like(cb.lower(
							root.join("reportedapplicant", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(cb.lower(
							root.join("reportedapplicant", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					searchPredicates.add(cb.like(cb.lower(
							root.join("reportedemployer", jakarta.persistence.criteria.JoinType.LEFT).get("fullname")),
							searchPattern));
					searchPredicates.add(cb.like(cb.lower(
							root.join("reportedemployer", jakarta.persistence.criteria.JoinType.LEFT).get("email")),
							searchPattern));
					predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
				}

				if (status != null && !status.trim().isEmpty()) {
					predicates.add(cb.equal(root.get("status"), status));
				}

				return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
			};

			Page<Report> reportPage = reportRepository.findAll(spec, pageable);
			List<ReportDTO> dtos = reportPage.getContent().stream().map(this::mapToReportDTO)
					.collect(Collectors.toList());

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("reports", dtos);
			responseData.put("totalPages", reportPage.getTotalPages());
			responseData.put("currentPage", reportPage.getNumber());
			responseData.put("totalItems", reportPage.getTotalElements());

			return new Response(true, "Lấy danh sách báo cáo vi phạm thành công", responseData);
		} catch (Exception e) {
			logger.error("Lỗi khi lấy danh sách báo cáo vi phạm: {}", e.getMessage(), e);
			return new Response(false, "Không thể lấy danh sách báo cáo vi phạm: " + e.getMessage(), null);
		}
	}

	@Override
	public Response getReportById(Long id) {
		try {
			Report report = reportRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo cáo vi phạm với ID: " + id));
			return new Response(true, "Lấy chi tiết báo cáo vi phạm thành công", mapToReportDTO(report));
		} catch (Exception e) {
			logger.error("Lỗi khi lấy chi tiết báo cáo vi phạm: {}", e.getMessage(), e);
			return new Response(false, "Không thể lấy chi tiết báo cáo vi phạm: " + e.getMessage(), null);
		}
	}

	@Override
	public Response resolveReport(Long id, ReportResolveRequest request) {
		try {
			Report report = reportRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo cáo vi phạm với ID: " + id));

			report.setStatus(EResponseStatus.valueOf(request.getStatus()));
			reportRepository.save(report);

			if ("RESPONDED".equals(request.getStatus()) && request.isViolationConfirmed()) {
				String reportedUsername = report.getReportedapplicant() != null
						? report.getReportedapplicant().getAccount().getUsername()
						: report.getReportedemployer() != null ? report.getReportedemployer().getAccount().getUsername()
								: null;

				if (reportedUsername != null) {
					Account account = accRepo.findById(reportedUsername).orElseThrow(() -> new IllegalArgumentException(
							"Không tìm thấy tài khoản với username: " + reportedUsername));
					account.setStatus(EAccountStatus.SUSPENDED);
					account.setSuspendedUntil(new Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
					accRepo.save(account);

					String email = report.getReportedapplicant() != null ? report.getReportedapplicant().getEmail()
							: report.getReportedemployer() != null ? report.getReportedemployer().getEmail() : null;

					if (email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches()) {
						String emailContent = String.format("<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>"
								+ "  <meta charset=\"UTF-8\"/>" + "  <style>"
								+ "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }"
								+ "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }"
								+ "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }"
								+ "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }"
								+ "  </style>" + "</head>" + "<body>" + "  <div class=\"container\">"
								+ "    <div class=\"header\">" + "      <h2>ClickWork</h2>" + "    </div>"
								+ "    <div class=\"content\">" + "      <p>Chào %s,</p>"
								+ "      <p>Tài khoản của bạn (%s) đã bị khóa do vi phạm chính sách (%s).</p>"
								+ "      <p>Thời gian khóa: 30 ngày, đến %s.</p>"
								+ "      <p>Nếu không có hành động khắc phục, tài khoản sẽ bị xóa sau thời gian này.</p>"
								+ "    </div>" + "    <div class=\"footer\">"
								+ "      © 2025 ClickWork. All rights reserved." + "    </div>" + "  </div>" + "</body>"
								+ "</html>",
								report.getReportedapplicant() != null ? report.getReportedapplicant().getFullname()
										: report.getReportedemployer() != null
												? report.getReportedemployer().getFullname()
												: "Người dùng",
								reportedUsername, report.getTitle(), account.getSuspendedUntil().toString());
						try {
							emailService.sendEmail(email, "Thông báo khóa tài khoản", emailContent);
							logger.info("Gửi email khóa tài khoản đến: {}", email);
						} catch (Exception e) {
							logger.warn("Không thể gửi email khóa tài khoản đến {}: {}", email, e.getMessage());
						}
					} else {
						logger.warn("Email không hợp lệ hoặc không tồn tại cho tài khoản bị báo cáo: {}",
								reportedUsername);
					}
				}
			}

			return new Response(true, "Xử lý báo cáo vi phạm thành công", mapToReportDTO(report));
		} catch (Exception e) {
			logger.error("Lỗi khi xử lý báo cáo vi phạm: {}", e.getMessage(), e);
			return new Response(false, "Không thể xử lý báo cáo vi phạm: " + e.getMessage(), null);
		}
	}

	@Override
	public Response updateAccount(String username, String role, String status) {
		try {
			logger.info("Updating account status: username={}, status='{}'", username, status);

			Account account = accRepo.findById(username).orElseThrow(
					() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));

			// Ignore role parameter (kept for compatibility)
			if (role != null && !role.trim().isEmpty()) {
				logger.warn("Role update requested for username {}, but role updates are disabled", username);
			}

			// Update status if provided
			if (status != null && !status.trim().isEmpty()) {
				try {
					EAccountStatus newStatus = EAccountStatus.valueOf(status.toUpperCase());
					if (account.getStatus() != newStatus) {
						if (newStatus == EAccountStatus.SUSPENDED) {
							account.setSuspendedUntil(
									new Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
							// Send email notification
							String email = account.getApplicant() != null ? account.getApplicant().getEmail()
									: account.getEmployer() != null ? account.getEmployer().getEmail()
											: account.getAdmin() != null ? account.getAdmin().getEmail() : null;

							if (email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches()) {
								String emailContent = String.format("<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>"
										+ "  <meta charset=\"UTF-8\"/>" + "  <style>"
										+ "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }"
										+ "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }"
										+ "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }"
										+ "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }"
										+ "  </style>" + "</head>" + "<body>" + "  <div class=\"container\">"
										+ "    <div class=\"header\">" + "      <h2>ClickWork</h2>" + "    </div>"
										+ "    <div class=\"content\">" + "      <p>Chào %s,</p>"
										+ "      <p>Tài khoản của bạn (%s) đã bị khóa do quyết định của quản trị viên.</p>"
										+ "      <p>Thời gian khóa: 30 ngày, đến %s.</p>"
										+ "      <p>Nếu không có hành động khắc phục, tài khoản sẽ bị xóa sau thời gian này.</p>"
										+ "    </div>" + "    <div class=\"footer\">"
										+ "      © 2025 ClickWork. All rights reserved." + "    </div>" + "  </div>"
										+ "</body>" + "</html>",
										account.getApplicant() != null ? account.getApplicant().getFullname()
												: account.getEmployer() != null ? account.getEmployer().getFullname()
														: account.getAdmin() != null ? account.getAdmin().getFullname()
																: "Người dùng",
										username, account.getSuspendedUntil().toString());
								try {
									emailService.sendEmail(email, "Thông báo khóa tài khoản", emailContent);
									logger.info("Gửi email khóa tài khoản đến: {}", email);
								} catch (Exception e) {
									logger.warn("Không thể gửi email khóa tài khoản đến {}: {}", email, e.getMessage());
								}
							} else {
								logger.warn("Email không hợp lệ hoặc không tồn tại cho tài khoản: {}", username);
							}
						} else {
							account.setSuspendedUntil(null);
						}
						account.setStatus(newStatus);
						logger.info("Updated status for {} to {}", username, newStatus);
					}
				} catch (IllegalArgumentException e) {
					return new Response(false, "Trạng thái không hợp lệ: " + status, null);
				}
			} else {
				return new Response(false, "Vui lòng cung cấp trạng thái để cập nhật", null);
			}

			accRepo.save(account);
			return new Response(true, "Cập nhật trạng thái tài khoản thành công", mapToAccountDTO(account));
		} catch (Exception e) {
			logger.error("Lỗi khi cập nhật trạng thái tài khoản: {}", e.getMessage(), e);
			return new Response(false, "Không thể cập nhật trạng thái tài khoản: " + e.getMessage(), null);
		}
	}

	@Override
	public Response createAdminAccount(RegisterRequest model) {
		try {
			logger.info("Creating new admin account: username={}", model.getUsername());

			// Validate inputs
			if (model.getUsername() == null || model.getUsername().trim().isEmpty()) {
				return new Response(false, "Username không được để trống", null);
			}
			if (model.getPassword() == null || model.getPassword().trim().isEmpty()) {
				return new Response(false, "Mật khẩu không được để trống", null);
			}
			if (model.getEmail() == null || model.getEmail().trim().isEmpty()) {
				return new Response(false, "Email không được để trống", null);
			}
			if (!EMAIL_PATTERN.matcher(model.getEmail()).matches()) {
				return new Response(false, "Email không hợp lệ", null);
			}

			// Check if username exists
			Optional<Account> optAcc = accRepo.findByUsername(model.getUsername());
			if (optAcc.isPresent()) {
				return new Response(false, "Tài khoản với username tương ứng đã tồn tại, vui lòng chọn username khác",
						null);
			}

			// Check if email exists
			if (isExistByEmail(model.getEmail())) {
				return new Response(false,
						"Email này đã được dùng để đăng ký một tài khoản khác, vui lòng chọn email khác", null);
			}

			// Check if admin already exists for this username
			Admin existingAdmin = adminRepo.findByAccountUsername(model.getUsername());
			if (existingAdmin != null) {
				return new Response(false, "Tài khoản quản trị viên với username này đã tồn tại", null);
			}

			// Create new account
			String hashedPassword = passwordUtil.hashPassword(model.getPassword());
			Account acc = new Account(model.getUsername(), hashedPassword, ERole.ADMIN);
			acc.setStatus(EAccountStatus.ACTIVE);

			// Create associated Admin entity
			Admin admin = new Admin();
			admin.setAccount(acc);
			admin.setEmail(model.getEmail());
			admin.setFullname("");
			admin.setPhonenum("");
			admin.setAvatar("/Uploads/avatar/user_default.png");
			acc.setAdmin(admin);

			// Save to database
			accRepo.save(acc);

			// Send welcome email
			String emailContent = String.format("<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>"
					+ "  <meta charset=\"UTF-8\"/>" + "  <style>"
					+ "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }"
					+ "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }"
					+ "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }"
					+ "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }"
					+ "  </style>" + "</head>" + "<body>" + "  <div class=\"container\">" + "    <div class=\"header\">"
					+ "      <h2>ClickWork</h2>" + "    </div>" + "    <div class=\"content\">"
					+ "      <p>Chào Quản trị viên,</p>"
					+ "      <p>Tài khoản quản trị viên của bạn (%s) đã được tạo thành công.</p>"
					+ "      <p>Vui lòng đăng nhập để bắt đầu quản lý hệ thống.</p>" + "    </div>"
					+ "    <div class=\"footer\">" + "      © 2025 ClickWork. All rights reserved." + "    </div>"
					+ "  </div>" + "</body>" + "</html>", model.getUsername());
			try {
				emailService.sendEmail(model.getEmail(), "Chào mừng quản trị viên mới", emailContent);
				logger.info("Gửi email chào mừng đến: {}", model.getEmail());
			} catch (Exception e) {
				logger.warn("Không thể gửi email chào mừng đến {}: {}", model.getEmail(), e.getMessage());
			}

			return new Response(true, "Tạo tài khoản quản trị viên thành công", mapToAccountDTO(acc));
		} catch (Exception e) {
			logger.error("Lỗi khi tạo tài khoản quản trị viên: {}", e.getMessage(), e);
			return new Response(false, "Không thể tạo tài khoản quản trị viên: " + e.getMessage(), null);
		}
	}

	@Override
	public Response getSystemEmails(String role, String search) {
		try {
			logger.info("Fetching system emails for role: {}, search: {}", role, search);
			List<Map<String, String>> emails = new ArrayList<>();

			// Prepare search pattern
			String searchPattern = search != null && !search.trim().isEmpty() ? "%" + search.toLowerCase() + "%" : null;

			if (role == null || role.trim().isEmpty() || role.equalsIgnoreCase("ALL")) {
				// Fetch all emails
				List<Applicant> applicants = searchPattern != null
						? appRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
						: appRepo.findAll();
				emails.addAll(applicants.stream().filter(a -> a.getEmail() != null && !a.getEmail().trim().isEmpty()
						&& EMAIL_PATTERN.matcher(a.getEmail()).matches()).map(a -> {
							Map<String, String> emailData = new HashMap<>();
							emailData.put("email", a.getEmail());
							emailData.put("fullname", a.getFullname() != null ? a.getFullname() : "");
							emailData.put("role", "APPLICANT");
							return emailData;
						}).collect(Collectors.toList()));

				List<Employer> employers = searchPattern != null
						? empRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
						: empRepo.findAll();
				emails.addAll(employers.stream().filter(e -> e.getEmail() != null && !e.getEmail().trim().isEmpty()
						&& EMAIL_PATTERN.matcher(e.getEmail()).matches()).map(e -> {
							Map<String, String> emailData = new HashMap<>();
							emailData.put("email", e.getEmail());
							emailData.put("fullname", e.getFullname() != null ? e.getFullname() : "");
							emailData.put("role", "EMPLOYER");
							return emailData;
						}).collect(Collectors.toList()));

				List<Admin> admins = searchPattern != null
						? adminRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
						: adminRepo.findAll();
				emails.addAll(admins.stream().filter(a -> a.getEmail() != null && !a.getEmail().trim().isEmpty()
						&& EMAIL_PATTERN.matcher(a.getEmail()).matches()).map(a -> {
							Map<String, String> emailData = new HashMap<>();
							emailData.put("email", a.getEmail());
							emailData.put("fullname", a.getFullname() != null ? a.getFullname() : "");
							emailData.put("role", "ADMIN");
							return emailData;
						}).collect(Collectors.toList()));
			} else if (role.equalsIgnoreCase("INACTIVE")) {
				// Fetch emails for inactive accounts
				List<Account> inactiveAccounts = accRepo.findAllByStatus(EAccountStatus.INACTIVE);
				emails.addAll(inactiveAccounts.stream().map(account -> {
					if (account.getApplicant() != null && (searchPattern == null
							|| account.getApplicant().getEmail().toLowerCase().contains(searchPattern.replace("%", ""))
							|| (account.getApplicant().getFullname() != null && account.getApplicant().getFullname()
									.toLowerCase().contains(searchPattern.replace("%", ""))))) {
						Map<String, String> emailData = new HashMap<>();
						emailData.put("email", account.getApplicant().getEmail());
						emailData.put("fullname",
								account.getApplicant().getFullname() != null ? account.getApplicant().getFullname()
										: "");
						emailData.put("role", "APPLICANT");
						return emailData;
					} else if (account.getEmployer() != null && (searchPattern == null
							|| account.getEmployer().getEmail().toLowerCase().contains(searchPattern.replace("%", ""))
							|| (account.getEmployer().getFullname() != null && account.getEmployer().getFullname()
									.toLowerCase().contains(searchPattern.replace("%", ""))))) {
						Map<String, String> emailData = new HashMap<>();
						emailData.put("email", account.getEmployer().getEmail());
						emailData.put("fullname",
								account.getEmployer().getFullname() != null ? account.getEmployer().getFullname() : "");
						emailData.put("role", "EMPLOYER");
						return emailData;
					} else if (account.getAdmin() != null && (searchPattern == null
							|| account.getAdmin().getEmail().toLowerCase().contains(searchPattern.replace("%", ""))
							|| (account.getAdmin().getFullname() != null && account.getAdmin().getFullname()
									.toLowerCase().contains(searchPattern.replace("%", ""))))) {
						Map<String, String> emailData = new HashMap<>();
						emailData.put("email", account.getAdmin().getEmail());
						emailData.put("fullname",
								account.getAdmin().getFullname() != null ? account.getAdmin().getFullname() : "");
						emailData.put("role", "ADMIN");
						return emailData;
					}
					return null;
				}).filter(data -> data != null && EMAIL_PATTERN.matcher(data.get("email")).matches())
						.collect(Collectors.toList()));
			} else {
				// Fetch emails by role
				ERole eRole;
				try {
					eRole = ERole.valueOf(role.toUpperCase());
				} catch (IllegalArgumentException e) {
					return new Response(false, "Vai trò không hợp lệ: " + role, null);
				}

				if (eRole == ERole.APPLICANT) {
					List<Applicant> applicants = searchPattern != null
							? appRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
							: appRepo.findAll();
					emails.addAll(applicants.stream().filter(a -> a.getEmail() != null && !a.getEmail().trim().isEmpty()
							&& EMAIL_PATTERN.matcher(a.getEmail()).matches()).map(a -> {
								Map<String, String> emailData = new HashMap<>();
								emailData.put("email", a.getEmail());
								emailData.put("fullname", a.getFullname() != null ? a.getFullname() : "");
								emailData.put("role", "APPLICANT");
								return emailData;
							}).collect(Collectors.toList()));
				} else if (eRole == ERole.EMPLOYER) {
					List<Employer> employers = searchPattern != null
							? empRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
							: empRepo.findAll();
					emails.addAll(employers.stream().filter(e -> e.getEmail() != null && !e.getEmail().trim().isEmpty()
							&& EMAIL_PATTERN.matcher(e.getEmail()).matches()).map(e -> {
								Map<String, String> emailData = new HashMap<>();
								emailData.put("email", e.getEmail());
								emailData.put("fullname", e.getFullname() != null ? e.getFullname() : "");
								emailData.put("role", "EMPLOYER");
								return emailData;
							}).collect(Collectors.toList()));
				} else if (eRole == ERole.ADMIN) {
					List<Admin> admins = searchPattern != null
							? adminRepo.findByEmailLikeIgnoreCaseOrFullnameLikeIgnoreCase(searchPattern, searchPattern)
							: adminRepo.findAll();
					emails.addAll(admins.stream().filter(a -> a.getEmail() != null && !a.getEmail().trim().isEmpty()
							&& EMAIL_PATTERN.matcher(a.getEmail()).matches()).map(a -> {
								Map<String, String> emailData = new HashMap<>();
								emailData.put("email", a.getEmail());
								emailData.put("fullname", a.getFullname() != null ? a.getFullname() : "");
								emailData.put("role", "ADMIN");
								return emailData;
							}).collect(Collectors.toList()));
				}
			}

			// Remove duplicates and sort
			List<Map<String, String>> uniqueEmails = emails.stream()
					.collect(Collectors.toMap(map -> map.get("email"), map -> map, (existing, replacement) -> existing))
					.values().stream().sorted((a, b) -> a.get("email").compareTo(b.get("email")))
					.collect(Collectors.toList());
			return new Response(true, "Lấy danh sách email thành công", uniqueEmails);
		} catch (Exception e) {
			logger.error("Lỗi khi lấy danh sách email: {}", e.getMessage(), e);
			return new Response(false, "Không thể lấy danh sách email: " + e.getMessage(), null);
		}
	}

	@Override
	public Response sendAdminEmail(String subject, String message, List<String> emails, String role) {
		try {
			logger.info("Sending admin email: subject='{}', role='{}', emails={}", subject, role, emails);

			// Validate inputs
			if (subject == null || subject.trim().isEmpty()) {
				return new Response(false, "Tiêu đề email không được để trống", null);
			}
			if (message == null || message.trim().isEmpty()) {
				return new Response(false, "Nội dung email không được để trống", null);
			}

			List<String> recipients = new ArrayList<>();

			// If emails are provided, validate and use them
			if (emails != null && !emails.isEmpty()) {
				for (String email : emails) {
					if (email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches()) {
						recipients.add(email);
					} else {
						logger.warn("Email không hợp lệ: {}", email);
					}
				}
			}

			// If role is provided, fetch emails by role
			if (role != null && !role.trim().isEmpty()) {
				Response emailResponse = getSystemEmails(role, null);
				if (!emailResponse.isStatus()) {
					return emailResponse;
				}
				List<Map<String, String>> roleEmails = (List<Map<String, String>>) emailResponse.getBody();
				recipients.addAll(roleEmails.stream().map(map -> map.get("email")).collect(Collectors.toList()));
			}

			// Remove duplicates
			recipients = recipients.stream().distinct().collect(Collectors.toList());

			if (recipients.isEmpty()) {
				return new Response(false, "Không có email hợp lệ để gửi", null);
			}

			// Build HTML content
			String htmlContent = String.format("<!DOCTYPE html>" + "<html lang=\"en\">" + "<head>"
					+ "  <meta charset=\"UTF-8\"/>" + "  <style>"
					+ "    .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4; }"
					+ "    .header { background-color: #2196F3; color: white; padding: 10px; text-align: center; border-radius: 4px 4px 0 0; }"
					+ "    .content { background-color: white; padding: 20px; border: 1px solid #ddd; border-top: none; }"
					+ "    .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }"
					+ "  </style>" + "</head>" + "<body>" + "  <div class=\"container\">" + "    <div class=\"header\">"
					+ "      <h2>ClickWork</h2>" + "    </div>" + "    <div class=\"content\">" + "      <p>%s</p>"
					+ "    </div>" + "    <div class=\"footer\">" + "      © 2025 ClickWork. All rights reserved."
					+ "    </div>" + "  </div>" + "</body>" + "</html>", message.replace("\n", "<br>"));

			// Send emails
			for (String email : recipients) {
				try {
					emailService.sendEmail(email, subject, htmlContent);
					logger.info("Gửi email đến: {}", email);
				} catch (Exception e) {
					logger.warn("Không thể gửi email đến {}: {}", email, e.getMessage());
				}
			}

			return new Response(true, "Gửi email thành công", null);
		} catch (Exception e) {
			logger.error("Lỗi khi gửi email: {}", e.getMessage(), e);
			return new Response(false, "Không thể gửi email: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Response> changePassword(ChangePasswordRequest request) {
		try {
			Optional<Account> account = accRepo.findById(request.getUsername());
			if (account.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new Response(false, "Tài khoản không tồn tại", null));
			}
			if (!passwordUtil.verifyPassword(request.getOldPassword(), account.get().getPassword())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new Response(false, "Mật khẩu cũ không chính xác", null));
			}
			if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new Response(false, "Mật khẩu mới không hợp lệ", null));
			}
			if (request.getNewPassword().equals(request.getOldPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new Response(false, "Mật khẩu mới không được giống mật khẩu cũ", null));
			}

			Account acc = account.get();

			acc.setPassword(passwordUtil.hashPassword(request.getNewPassword()));
			accRepo.save(acc);
			return ResponseEntity.ok(new Response(true, "Đổi mật khẩu thành công", null));

		} catch (Exception e) {
			logger.error("Lỗi khi đổi mật khẩu: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Không thể đổi mật khẩu: " + e.getMessage(), null));
		}
	}

	@Override
	public Response createReport(ReportRequestDTO dto, String senderUsername) {
		try {
			logger.info("Creating new report from user: {}", senderUsername);

			// Validate
			if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
				return new Response(false, "Tiêu đề không được để trống", null);
			}
			if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
				return new Response(false, "Nội dung không được để trống", null);
			}
			if (dto.getReportedUsername() == null || dto.getReportedUsername().trim().isEmpty()) {
				return new Response(false, "Không tìm thấy thông tin người bị báo cáo", null);
			}

			// Tạo báo cáo mới
			Report report = new Report();
			report.setTitle(dto.getTitle());
			report.setContent(dto.getContent());
			report.setSendat(new Timestamp(System.currentTimeMillis()));
			report.setStatus(EResponseStatus.PENDING);

			// Tìm thông tin người gửi báo cáo
			Optional<Account> senderAccount = accRepo.findByUsername(senderUsername);
			if (senderAccount.isEmpty()) {
				return new Response(false, "Không tìm thấy thông tin người gửi báo cáo", null);
			}

			// Tìm thông tin người bị báo cáo
			Optional<Account> reportedAccount = accRepo.findByUsername(dto.getReportedUsername());
			if (reportedAccount.isEmpty()) {
				return new Response(false, "Không tìm thấy thông tin người bị báo cáo", null);
			}

			// Xác định và thiết lập người gửi/người bị báo cáo
			Account sender = senderAccount.get();
			Account reported = reportedAccount.get();

			if (sender.getRole() == ERole.APPLICANT) {
				Applicant applicant = appRepo.findByAccount_Username(senderUsername);
				if (applicant == null) {
					return new Response(false, "Không tìm thấy thông tin ứng viên", null);
				}
				report.setApplicant(applicant);
			} else if (sender.getRole() == ERole.EMPLOYER) {
				Employer employer = empRepo.findByAccount_Username(senderUsername)
						.orElse(null);
				if (employer == null) {
					return new Response(false, "Không tìm thấy thông tin nhà tuyển dụng", null);
				}
				report.setEmployer(employer);
			} else {
				return new Response(false, "Vai trò không hợp lệ để tạo báo cáo", null);
			}

			if (reported.getRole() == ERole.APPLICANT) {
				Applicant reportedApplicant = appRepo.findByAccount_Username(dto.getReportedUsername());
				if (reportedApplicant == null) {
					return new Response(false, "Không tìm thấy thông tin ứng viên bị báo cáo", null);
				}
				report.setReportedapplicant(reportedApplicant);
			} else if (reported.getRole() == ERole.EMPLOYER) {
				Employer reportedEmployer = empRepo.findByAccount_Username(dto.getReportedUsername())
						.orElse(null);
				if (reportedEmployer == null) {
					return new Response(false, "Không tìm thấy thông tin nhà tuyển dụng bị báo cáo", null);
				}
				report.setReportedemployer(reportedEmployer);
			} else {
				return new Response(false, "Vai trò không hợp lệ để bị báo cáo", null);
			}

			// Lưu báo cáo
			reportRepository.save(report);

			// Gửi thông báo đến tất cả admin
			sendReportNotificationToAdmins(report);

			return new Response(true, "Báo cáo vi phạm đã được gửi thành công", null);
		} catch (Exception e) {
			logger.error("Lỗi khi tạo báo cáo vi phạm: {}", e.getMessage(), e);
			return new Response(false, "Không thể tạo báo cáo vi phạm: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Response> activeAccount(String username) {
		try {
			username = username.replaceAll("\"", "");
			Optional<Account> optaccount = accRepo.findByUsername(username);
			if (optaccount.isEmpty()) {
				return ResponseEntity.ok()
						.body(new Response(false, "Tài khoản không tồn tại", null));
			}
			Account account = optaccount.get();

			account.setStatus(EAccountStatus.ACTIVE);
			accRepo.save(account);

			return ResponseEntity.ok().body(new Response(true, "Kích hoạt tài khoản thành công", null));
		} catch (Exception e) {
			logger.error("Lỗi khi kích hoạt tài khoản: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(false, "Kích hoạt tài khoản thất bại", null));
		}
	}

	// Phương thức gửi thông báo đến tất cả admin
	private void sendReportNotificationToAdmins(Report report) {
		try {
			// Lấy danh sách admin
			List<Admin> admins = adminRepo.findAll();
			if (admins.isEmpty()) {
				logger.warn("Không có admin nào trong hệ thống để gửi thông báo");
				return;
			}

			// Tạo thông báo
			Notification notification = new Notification();
			notification.setTitle("Báo cáo vi phạm mới");

			String reportedUser = report.getReportedapplicant() != null
					? report.getReportedapplicant().getFullname() + " (Ứng viên)"
					: report.getReportedemployer() != null
							? report.getReportedemployer().getFullname() + " (Nhà tuyển dụng)"
							: "Người dùng";

			String reporterUser = report.getApplicant() != null
					? report.getApplicant().getFullname() + " (Ứng viên)"
					: report.getEmployer() != null
							? report.getEmployer().getFullname() + " (Nhà tuyển dụng)"
							: "Người dùng";

			notification.setContent(String.format("Báo cáo mới (ID: %d) từ %s về người dùng %s với nội dung: %s",
					report.getId(), reporterUser, reportedUser, report.getTitle()));

			notification.setType(ENotiType.SYSTEM);
			notification.setSendat(new Timestamp(System.currentTimeMillis()));
			notification.setRead(false);
			notification.setAdmins(admins);

			notificationRepository.save(notification);
			logger.info("Đã gửi thông báo về báo cáo vi phạm đến {} admin", admins.size());
		} catch (Exception e) {
			logger.error("Lỗi khi gửi thông báo đến admin: {}", e.getMessage(), e);

		}
	}
}