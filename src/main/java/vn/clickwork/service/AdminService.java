package vn.clickwork.service;

import vn.clickwork.entity.Admin;

public interface AdminService {
    Admin findByAccount_Username(String username);

    void updateAdmin(Admin admin);  // Phương thức cập nhật thông tin Admin
}
