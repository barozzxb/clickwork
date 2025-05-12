package vn.clickwork.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployerDTO {
    private Long id;
    private String fullname;
    private String email;
    private String phone;
    private List<AddressDTO> addresses;
    private String logo;
    private String website;

    // Phương thức tiện ích để lấy địa chỉ chính
    public String getMainAddress() {
        if (addresses != null && !addresses.isEmpty()) {
            return addresses.get(0).getFullAddress();
        }
        return null;
    }
}
