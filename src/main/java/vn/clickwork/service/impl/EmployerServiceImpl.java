package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.clickwork.entity.Account;
import vn.clickwork.entity.Employer;
import vn.clickwork.entity.Address;
import vn.clickwork.model.Response;
import vn.clickwork.model.request.EmployerDetailRequest;
import vn.clickwork.model.dto.EmployerProfileDTO;
import vn.clickwork.repository.AccountRepository;
import vn.clickwork.repository.EmployerRepository;
import vn.clickwork.repository.AddressRepository;
import vn.clickwork.service.EmployerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public ResponseEntity<Response> update(EmployerDetailRequest employer) {
        try {
            Optional<Employer> optional = employerRepository.findById(employer.getId());
            if (!optional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy thông tin", null));
            }

            Employer existing = optional.get();

            // Cập nhật thông tin
            existing.setFullname(employer.getFullname());
            existing.setDatefounded(employer.getDatefounded());
            existing.setPhonenum(employer.getPhonenum());
            existing.setEmail(employer.getEmail());
            existing.setWebsite(employer.getWebsite());
            existing.setTaxnumber(employer.getTaxnumber());
            existing.setField(employer.getField());
            existing.setWorkingdays(employer.getWorkingdays());
            existing.setCompanysize(employer.getCompanysize());
            existing.setSociallink(employer.getSociallink());
            existing.setOverview(employer.getOverview());

            employerRepository.save(existing);
            return ResponseEntity.ok(new Response(true, "Cập nhật thành công", existing));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Cập nhật thất bại", null));
        }
    }

    @Override
    public Employer findByUsername(String username) {
        Optional<Employer> optional = employerRepository.findByAccount_Username(username);
        return optional.orElse(null);
    }

    @Override
    public ResponseEntity<Response> updateAvatar(String username, MultipartFile file) {
        Optional<Account> accOpt = accountRepo.findByUsername(username);
        if (!accOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy người dùng", null));
        }

        Employer employer = employerRepository.findByAccount(accOpt.get());

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/avatar");
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/avatar/" + fileName;
            employer.setAvatar(fileUrl);
            employerRepository.save(employer);

            return ResponseEntity.ok(new Response(true, "Upload avatar thành công", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Lỗi khi upload ảnh", null));
        }
    }

    @Override
    public ResponseEntity<Response> findAll() {
        List<Employer> employers = employerRepository.findAll();
        if (employers.isEmpty()) {
            return new ResponseEntity<Response>(new Response(true, "Danh sách trống", null), HttpStatus.OK);
        }
        return new ResponseEntity<Response>(new Response(true, "Lấy danh sách thành công", employers), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response> save(Employer entity) {
        try {
            employerRepository.save(entity);
            return new ResponseEntity<Response>(new Response(true, "Cập nhật thông tin thành công", entity),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Response>(new Response(false, "Cập nhật thông tin thất bại", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getEmployerEmailByUsername(String username) {
        Account acc = accountRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        if (acc.getEmployer() == null) {
            throw new IllegalStateException("Tài khoản không phải employer");
        }
        return acc.getEmployer().getEmail();
    }

    @Override
    public EmployerProfileDTO getProfile(String username) {
        Employer employer = employerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        EmployerProfileDTO dto = new EmployerProfileDTO();
        dto.setUsername(employer.getAccount().getUsername());
        dto.setFullname(employer.getFullname());
        dto.setPhonenum(employer.getPhonenum());
        dto.setAvatar(employer.getAvatar());
        dto.setWebsite(employer.getWebsite());
        dto.setTaxnumber(employer.getTaxnumber());
        dto.setField(employer.getField());
        dto.setWorkingdays(employer.getWorkingdays());
        dto.setCompanysize(employer.getCompanysize());
        dto.setSociallink(employer.getSociallink());
        dto.setOverview(employer.getOverview());

        // Map address
        List<EmployerProfileDTO.AddressDTO> addressDTOs = employer.getAddresses().stream().map(addr -> {
            EmployerProfileDTO.AddressDTO a = new EmployerProfileDTO.AddressDTO();
            a.setId(addr.getId());
            a.setNation(addr.getNation());
            a.setProvince(addr.getProvince());
            a.setDistrict(addr.getDistrict());
            a.setVillage(addr.getVillage());
            a.setDetail(addr.getDetail());
            return a;
        }).toList();
        dto.setAddresses(addressDTOs);
        return dto;
    }

    @Override
    public void updateProfile(String username, EmployerProfileDTO dto) {
        Employer employer = employerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        // Không cập nhật username, email
        employer.setFullname(dto.getFullname());
        employer.setPhonenum(dto.getPhonenum());
        employer.setAvatar(dto.getAvatar());
        employer.setWebsite(dto.getWebsite());
        employer.setTaxnumber(dto.getTaxnumber());
        employer.setField(dto.getField());
        employer.setWorkingdays(dto.getWorkingdays());
        employer.setCompanysize(dto.getCompanysize());
        employer.setSociallink(dto.getSociallink());
        employer.setOverview(dto.getOverview());
        employerRepository.save(employer);
    }

    @Override
    public void addAddress(String username, EmployerProfileDTO.AddressDTO addressDTO) {
        Employer employer = employerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        Address address = new Address();
        address.setNation(addressDTO.getNation());
        address.setProvince(addressDTO.getProvince());
        address.setDistrict(addressDTO.getDistrict());
        address.setVillage(addressDTO.getVillage());
        address.setDetail(addressDTO.getDetail());
        address.setEmployer(employer);
        addressRepository.save(address);
    }

    @Override
    public void updateAddress(String username, Long addressId, EmployerProfileDTO.AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        // Có thể kiểm tra
        // address.getEmployer().getAccount().getUsername().equals(username)
        address.setNation(addressDTO.getNation());
        address.setProvince(addressDTO.getProvince());
        address.setDistrict(addressDTO.getDistrict());
        address.setVillage(addressDTO.getVillage());
        address.setDetail(addressDTO.getDetail());
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(String username, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        // Có thể kiểm tra
        // address.getEmployer().getAccount().getUsername().equals(username)
        addressRepository.delete(address);
    }
}
