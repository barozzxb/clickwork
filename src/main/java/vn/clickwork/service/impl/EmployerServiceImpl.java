package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Employer;
import vn.clickwork.model.Response;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.service.EmployerService;

import java.util.Optional;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    @Override
    public ResponseEntity<Response> update(Employer entity, MultipartFile avatarFile) {
        // Logic cập nhật employer + xử lý file avatar nếu cần
        try {
            // TODO: xử lý file avatar nếu có (tương tự phần xử lý avatar của applicant)
            employerRepository.save(entity);
            return ResponseEntity.ok(new Response(true, "Cập nhật thành công", entity));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new Response(false, "Cập nhật thất bại", null));
        }
    }

    @Override
    public Employer findByUsername(String username) {
        Optional<Employer> optional = employerRepository.findByAccount_Username(username);
        return optional.orElse(null);
    }
}
