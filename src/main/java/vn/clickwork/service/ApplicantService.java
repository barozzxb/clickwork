package vn.clickwork.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.dto.SupportRequestDTO;
import vn.clickwork.entity.Applicant;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.ApplicantDetailRequest;
import vn.clickwork.model.request.ChangePasswordRequest;

public interface ApplicantService {

	ResponseEntity<Response> update(ApplicantDetailRequest entity);

	long count();

	ResponseEntity<Response> findById(Long id);

	ResponseEntity<Response> findAll();

	ResponseEntity<Response> save(Applicant entity);
	
//	ResponseEntity<Response> save(Applicant entity);

	ResponseEntity<Response> findByEmail(String email);

	ResponseEntity<Response> findByUsername(String username);

	ResponseEntity<Response> updateAvatar(String username, MultipartFile file);

    ResponseEntity<Response> requestSupport(SupportRequestDTO supportRequestDTO);
 
}
