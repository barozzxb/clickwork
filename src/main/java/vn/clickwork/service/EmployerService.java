package vn.clickwork.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Employer;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.EmployerDetailRequest;
import vn.clickwork.model.dto.EmployerProfileDTO;

public interface EmployerService {

    ResponseEntity<Response> update(EmployerDetailRequest employer);

    Employer findByUsername(String username);

    ResponseEntity<Response> updateAvatar(String username, MultipartFile file);

    ResponseEntity<Response> findAll();

    ResponseEntity<Response> save(Employer entity);

    String getEmployerEmailByUsername(String username);

    EmployerProfileDTO getProfile(String username);

    void updateProfile(String username, EmployerProfileDTO dto);

    // Address management
    void addAddress(String username, EmployerProfileDTO.AddressDTO addressDTO);

    void updateAddress(String username, Long addressId, EmployerProfileDTO.AddressDTO addressDTO);

    void deleteAddress(String username, Long addressId);

	ResponseEntity<Response> findByEUsername(String username);
}
