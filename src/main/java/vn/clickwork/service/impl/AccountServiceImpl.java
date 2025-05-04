package vn.clickwork.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.enumeration.ERole;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.LoginRequest;
import vn.clickwork.model.request.RegisterRequest;
import vn.clickwork.model.request.ResetPasswordRequest;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.AccountService;
import vn.clickwork.util.JwtUtils;
import vn.clickwork.util.PasswordUtil;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accRepo;

	@Autowired
	ApplicantRepository appRepo;

	@Autowired
	EmployerRepository empRepo;

	@Autowired
	PasswordUtil passwordUtil;

	@Autowired
	JwtUtils jwtUtils;

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
				// Tạo JWT token cho tài khoản
				String token = jwtUtils.generateToken(acc.getUsername(), acc.getRole());

				Map<String, Object> body = new HashMap<>();
//	            body.put("username", acc.getUsername());
//	            body.put("role", acc.getRole());
				body.put("token", token);

				return new Response(true, "Đăng nhập thành công", body);
			} else {
				return new Response(false, "Sai thông tin đăng nhập", null);
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
		}
		accRepo.save(acc);
		return new Response(true, "Đăng ký thành công", acc);
	}
	
	@Override
	public ResponseEntity<Response> requestResetPassword(@RequestBody String email) {
		if (email == null) {
			return new ResponseEntity<Response>(new Response(false, "Bạn phải nhập email để tiếp tục", null),
					HttpStatus.BAD_REQUEST);
		}

		Account acc = getAccountByEmail(email);
		if (acc == null) {
			return new ResponseEntity<Response>(new Response(false, "Email chưa được dùng để đăng ký. Vui lòng thử lại", null),
					HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Response>(new Response(true, "Email hợp lệ", null),
				HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordRequest model) {

		Account acc = getAccountByEmail(model.getEmail());

		String newPassword = passwordUtil.hashPassword(model.getPassword());
		acc.setPassword(newPassword);
		accRepo.save(acc);
		return new ResponseEntity<Response>(new Response(true, "Mật khẩu mới đã được gửi đến email của bạn", null),
				HttpStatus.OK);
	}

	private boolean isExistByEmail(String email) {
		return appRepo.findByEmail(email) != null || empRepo.findByEmail(email) != null;
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
		return null;
	}
}
