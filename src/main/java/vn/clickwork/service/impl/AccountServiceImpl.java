package vn.clickwork.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Applicant;
import vn.clickwork.entity.Employer;
import vn.clickwork.enumeration.ERole;
import vn.clickwork.model.LoginModel;
import vn.clickwork.model.RegisterModel;
import vn.clickwork.model.Response;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.ApplicantRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.AccountService;
import vn.clickwork.util.PasswordUtil;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	AccountRepository accRepo;
	
	@Autowired
	ApplicantRepository appRepo;
	
	@Autowired
	EmployerRepository empRepo;
	
	@Autowired
	PasswordUtil passwordUtil;

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
	public Response login(LoginModel loginModel) {
		Optional<Account> optAcc = this.findByUsername(loginModel.getUsername());
		if (optAcc.isPresent()) {
			Account acc = optAcc.get();
			if (passwordUtil.verifyPassword(loginModel.getPassword(), acc.getPassword()))
				return new Response(true, "Đăng nhập thành công", acc);
			else {
				return new Response(false, "Mật khẩu không chính xác", null);
			}
		}
		return new Response(false, "Tài khoản không tồn tại, vui lòng thử lại hoặc tạo tài khoản mới", null);
	}

	@Override
	public Response register(RegisterModel model) {
		Optional<Account> optAcc = this.findByUsername(model.getUsername());
		if (optAcc.isPresent()) {
			return new Response(false, "Tài khoản với username tương ứng đã tồn tại, vui lòng chọn username khác", null);
		} else {
			String hashedPassword = passwordUtil.hashPassword(model.getPassword());
			Account acc = new Account(model.getUsername(), hashedPassword, model.getRole());
			if (model.getRole() == ERole.APPLICANT) {
				Applicant applicant = new Applicant();
				applicant.setAccount(acc);
				applicant.setEmail(model.getEmail());
				acc.setApplicant(applicant);
			} else if (model.getRole() == ERole.EMPLOYER) {
				 Employer employer = new Employer();
				 employer.setAccount(acc);
				 employer.setEmail(model.getEmail());
				 acc.setEmployer(employer);
			}
			accRepo.save(acc);
			return new Response(true, "Đăng ký thành công", acc);
		}
	}
}
