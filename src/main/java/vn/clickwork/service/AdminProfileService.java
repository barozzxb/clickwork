package vn.clickwork.service;

import vn.clickwork.model.dto.AdminProfileDTO;
import vn.clickwork.model.request.AddressRequest;
import vn.clickwork.model.request.PasswordChangeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminProfileService {

    AdminProfileDTO getAdminProfile(String username);

    AdminProfileDTO updateProfile(String username, String fullname, String phonenum, MultipartFile avatarFile);

    boolean changePassword(String username, PasswordChangeRequest request);

    List<AdminProfileDTO.AddressDTO> getAddresses(String username);

    AdminProfileDTO.AddressDTO addAddress(String username, AddressRequest request);

    AdminProfileDTO.AddressDTO updateAddress(String username, Long addressId, AddressRequest request);

    boolean deleteAddress(String username, Long addressId);
}