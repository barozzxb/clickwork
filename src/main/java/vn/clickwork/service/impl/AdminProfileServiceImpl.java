package vn.clickwork.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Address;
import vn.clickwork.entity.Admin;
import vn.clickwork.model.dto.AdminProfileDTO;
import vn.clickwork.model.request.AddressRequest;
import vn.clickwork.model.request.PasswordChangeRequest;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.AddressRepository;
import vn.clickwork.repository.AdminRepository;
import vn.clickwork.service.AdminProfileService;
import vn.clickwork.util.PasswordUtil;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

    private static final Logger logger = LoggerFactory.getLogger(AdminProfileServiceImpl.class);

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    // Define the absolute path for avatar uploads
    private static final String UPLOAD_DIR = "uploads/avatar/";
    private static final String AVATAR_URL_PREFIX = "/uploads/avatar/";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public AdminProfileDTO getAdminProfile(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.info("Fetching profile for username: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        AdminProfileDTO dto = mapToAdminProfileDTO(admin);
        logger.info("Successfully retrieved profile for username: {}", username);
        return dto;
    }

    @Override
    @Transactional
    public AdminProfileDTO updateProfile(String username, String fullname, String phonenum, MultipartFile avatarFile) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (fullname == null || fullname.trim().isEmpty()) {
            logger.error("Fullname is null or empty");
            throw new IllegalArgumentException("Fullname cannot be null or empty");
        }
        if (fullname.length() > 255) {
            logger.error("Fullname exceeds 255 characters");
            throw new IllegalArgumentException("Fullname cannot exceed 255 characters");
        }
        if (phonenum != null && !phonenum.trim().isEmpty() && !PHONE_PATTERN.matcher(phonenum).matches()) {
            logger.error("Invalid phone number format: {}", phonenum);
            throw new IllegalArgumentException("Phone number must be 10-15 digits");
        }

        logger.info("Updating profile for username: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        // Update basic info
        admin.setFullname(fullname);
        admin.setPhonenum(phonenum);

        // Handle avatar upload if provided
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Validate file type
                String contentType = avatarFile.getContentType();
                if (!List.of("image/jpeg", "image/png", "image/gif", "image/jpg").contains(contentType)) {
                    logger.error("Invalid file type: {}", contentType);
                    throw new IllegalArgumentException("Only JPEG, PNG, JPG, or GIF files are allowed");
                }

                // Validate file size (max 2MB)
                if (avatarFile.getSize() > 2 * 1024 * 1024) {
                    logger.error("File size exceeds 2MB");
                    throw new IllegalArgumentException("File size must not exceed 2MB");
                }

                // Delete existing avatar if present
                if (admin.getAvatar() != null && !admin.getAvatar().isEmpty()) {
                    try {
                        // Extract filename from avatar URL
                        String existingAvatarPath = admin.getAvatar();
                        if (existingAvatarPath.startsWith(AVATAR_URL_PREFIX)) {
                            existingAvatarPath = existingAvatarPath.substring(AVATAR_URL_PREFIX.length());
                        }

                        // Create absolute path to the file
                        String projectRoot = new File("").getAbsolutePath();
                        File existingFile = new File(projectRoot, UPLOAD_DIR + existingAvatarPath);
                        if (existingFile.exists()) {
                            existingFile.delete();
                            logger.info("Deleted existing avatar: {}", existingAvatarPath);
                        } else {
                            logger.warn("Existing avatar file not found: {}", existingAvatarPath);
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to delete existing avatar: {}", e.getMessage());
                    }
                }

                // Generate unique filename
                String originalFilename = avatarFile.getOriginalFilename();
                String fileExtension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                        : ".png";
                String fileName = UUID.randomUUID() + fileExtension;

                // Get the project root directory (where src and Uploads are located)
                String projectRoot = new File("").getAbsolutePath();
                File uploadDir = new File(projectRoot, UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                    logger.info("Created avatar upload directory: {}", uploadDir.getAbsolutePath());
                }

                // Save the file
                File destFile = new File(uploadDir, fileName);
                avatarFile.transferTo(destFile);
                logger.info("Saved avatar file to: {}", destFile.getAbsolutePath());

                // Construct URL for database
                String fileUrl = AVATAR_URL_PREFIX + fileName;
                admin.setAvatar(fileUrl);
                logger.info("Uploaded new avatar: {}", fileUrl);
            } catch (Exception e) {
                logger.error("Failed to upload avatar: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
            }
        }

        // Save to repository
        Admin updatedAdmin = adminRepository.save(admin);
        AdminProfileDTO dto = mapToAdminProfileDTO(updatedAdmin);
        logger.info("Successfully updated profile for username: {}", username);
        return dto;
    }

    @Override
    @Transactional
    public boolean changePassword(String username, PasswordChangeRequest request) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            logger.error("Current password is null or empty");
            throw new IllegalArgumentException("Current password cannot be null or empty");
        }
        if (request.getNewPassword() == null || !PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            logger.error("Invalid new password format");
            throw new IllegalArgumentException("New password must be at least 8 characters and contain at least one letter and one number");
        }

        logger.info("Changing password for username: {}", username);
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (!accountOpt.isPresent()) {
            logger.error("Account not found for username: {}", username);
            throw new RuntimeException("Account not found");
        }

        Account account = accountOpt.get();

        // Verify current password
        if (!passwordUtil.verifyPassword(request.getCurrentPassword(), account.getPassword())) {
            logger.error("Incorrect current password for username: {}", username);
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        String hashedPassword = passwordUtil.hashPassword(request.getNewPassword());
        account.setPassword(hashedPassword);

        // Save to repository
        accountRepository.save(account);
        logger.info("Password changed successfully for username: {}", username);
        return true;
    }

    @Override
    public List<AdminProfileDTO.AddressDTO> getAddresses(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.info("Fetching addresses for username: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        List<AdminProfileDTO.AddressDTO> addressDTOs = addressRepository.findByAdminId(admin.getId())
                .stream()
                .map(this::mapToAddressDTO)
                .collect(Collectors.toList());
        logger.info("Successfully retrieved {} addresses for username: {}", addressDTOs.size(), username);
        return addressDTOs;
    }

    @Override
    @Transactional
    public AdminProfileDTO.AddressDTO addAddress(String username, AddressRequest request) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        validateAddressRequest(request);

        logger.info("Adding address for username: {}", username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        // Create new address
        Address address = new Address();
        address.setNation(request.getNation());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setVillage(request.getVillage());
        address.setDetail(request.getDetail());
        address.setAdmin(admin);

        // Save to repository
        Address savedAddress = addressRepository.save(address);
        AdminProfileDTO.AddressDTO addressDTO = mapToAddressDTO(savedAddress);
        logger.info("Successfully added address with ID {} for username: {}", savedAddress.getId(), username);
        return addressDTO;
    }

    @Override
    @Transactional
    public AdminProfileDTO.AddressDTO updateAddress(String username, Long addressId, AddressRequest request) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (addressId == null) {
            logger.error("Address ID is null");
            throw new IllegalArgumentException("Address ID cannot be null");
        }
        validateAddressRequest(request);

        logger.info("Updating address ID {} for username: {}", addressId, username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        Optional<Address> addressOpt = addressRepository.findByIdAndAdminId(addressId, admin.getId());
        if (!addressOpt.isPresent()) {
            logger.error("Address not found with ID {} for username: {}", addressId, username);
            throw new RuntimeException("Address not found or you don't have permission");
        }

        Address address = addressOpt.get();

        // Update address fields
        address.setNation(request.getNation());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setVillage(request.getVillage());
        address.setDetail(request.getDetail());

        // Save to repository
        Address updatedAddress = addressRepository.save(address);
        AdminProfileDTO.AddressDTO addressDTO = mapToAddressDTO(updatedAddress);
        logger.info("Successfully updated address ID {} for username: {}", addressId, username);
        return addressDTO;
    }

    @Override
    @Transactional
    public boolean deleteAddress(String username, Long addressId) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (addressId == null) {
            logger.error("Address ID is null");
            throw new IllegalArgumentException("Address ID cannot be null");
        }

        logger.info("Deleting address ID {} for username: {}", addressId, username);
        Admin admin = adminRepository.findByAccountUsername(username);
        if (admin == null) {
            logger.error("Admin not found for username: {}", username);
            throw new RuntimeException("Admin profile not found");
        }

        Optional<Address> addressOpt = addressRepository.findByIdAndAdminId(addressId, admin.getId());
        if (!addressOpt.isPresent()) {
            logger.error("Address not found with ID {} for username: {}", addressId, username);
            throw new RuntimeException("Address not found or you don't have permission");
        }

        addressRepository.delete(addressOpt.get());
        logger.info("Successfully deleted address ID {} for username: {}", addressId, username);
        return true;
    }

    private AdminProfileDTO mapToAdminProfileDTO(Admin admin) {
        AdminProfileDTO dto = new AdminProfileDTO();
        dto.setUsername(admin.getAccount() != null ? admin.getAccount().getUsername() : null);
        dto.setFullname(admin.getFullname());
        dto.setEmail(admin.getEmail());
        dto.setPhonenum(admin.getPhonenum());
        dto.setAvatar(admin.getAvatar());
        dto.setAddresses(admin.getAddresses().stream()
                .map(this::mapToAddressDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private AdminProfileDTO.AddressDTO mapToAddressDTO(Address address) {
        return new AdminProfileDTO.AddressDTO(
                address.getId(),
                address.getNation(),
                address.getProvince(),
                address.getDistrict(),
                address.getVillage(),
                address.getDetail()
        );
    }

    private void validateAddressRequest(AddressRequest request) {
        if (request.getNation() == null || request.getNation().trim().isEmpty()) {
            logger.error("Nation is null or empty");
            throw new IllegalArgumentException("Nation cannot be null or empty");
        }
        if (request.getNation().length() > 255) {
            logger.error("Nation exceeds 255 characters");
            throw new IllegalArgumentException("Nation cannot exceed 255 characters");
        }
        if (request.getProvince() == null || request.getProvince().trim().isEmpty()) {
            logger.error("Province is null or empty");
            throw new IllegalArgumentException("Province cannot be null or empty");
        }
        if (request.getProvince().length() > 255) {
            logger.error("Province exceeds 255 characters");
            throw new IllegalArgumentException("Province cannot exceed 255 characters");
        }
        if (request.getDistrict() == null || request.getDistrict().trim().isEmpty()) {
            logger.error("District is null or empty");
            throw new IllegalArgumentException("District cannot be null or empty");
        }
        if (request.getDistrict().length() > 255) {
            logger.error("District exceeds 255 characters");
            throw new IllegalArgumentException("District cannot exceed 255 characters");
        }
        if (request.getVillage() == null || request.getVillage().trim().isEmpty()) {
            logger.error("Village is null or empty");
            throw new IllegalArgumentException("Village cannot be null or empty");
        }
        if (request.getVillage().length() > 255) {
            logger.error("Village exceeds 255 characters");
            throw new IllegalArgumentException("Village cannot exceed 255 characters");
        }
        if (request.getDetail() == null || request.getDetail().trim().isEmpty()) {
            logger.error("Detail is null or empty");
            throw new IllegalArgumentException("Detail cannot be null or empty");
        }
        if (request.getDetail().length() > 255) {
            logger.error("Detail exceeds 255 characters");
            throw new IllegalArgumentException("Detail cannot exceed 255 characters");
        }
    }
}
