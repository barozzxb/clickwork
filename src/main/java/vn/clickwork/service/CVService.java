package vn.clickwork.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.model.Response;

public interface CVService {

	ResponseEntity<Response> findByUsername(@RequestBody String username);

	ResponseEntity<Response> uploadCV(String username, String filename, MultipartFile file);

	ResponseEntity<Response> deleteCV(Long id);

}
