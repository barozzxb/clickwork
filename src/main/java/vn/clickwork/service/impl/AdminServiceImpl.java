package vn.clickwork.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.clickwork.entity.Admin;
import vn.clickwork.repository.AdminRepository;  // Giả sử bạn có repository cho Admin
import vn.clickwork.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findByAccount_Username(String username) {
        return adminRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    @Override
    public void updateAdmin(Admin admin) {
        // Tìm admin theo id (hoặc có thể là username tùy thuộc vào yêu cầu của bạn)
        Admin existingAdmin = adminRepository.findById(admin.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Cập nhật các trường của admin nếu cần thiết
        existingAdmin.setAddresses(admin.getAddresses());
        existingAdmin.setNotifications(admin.getNotifications());
        existingAdmin.setAccount(admin.getAccount());
        
        // Lưu lại admin đã cập nhật
        adminRepository.save(existingAdmin);
    }
}
