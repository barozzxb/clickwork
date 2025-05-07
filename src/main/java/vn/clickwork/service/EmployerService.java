package vn.clickwork.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Employer;
import vn.clickwork.model.Response;

public interface EmployerService {

	ResponseEntity<Response> update(Employer entity, MultipartFile avatarFile);

    Employer findByUsername(String username);

}
