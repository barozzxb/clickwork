package vn.clickwork.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Employer;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.EmployerDetailRequest;

public interface EmployerService {

	ResponseEntity<Response> update(EmployerDetailRequest employer);

    Employer findByUsername(String username);

    ResponseEntity<Response> updateAvatar(String username, MultipartFile file);

	ResponseEntity<Response> findAll();

	ResponseEntity<Response> save(Employer entity);
}
