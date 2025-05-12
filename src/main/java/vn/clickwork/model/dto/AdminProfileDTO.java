package vn.clickwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminProfileDTO {
    private String username;
    private String fullname;
    private String email;
    private String phonenum;
    private String avatar;
    private List<AddressDTO> addresses;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDTO {
        private Long id;
        private String nation;
        private String province;
        private String district;
        private String village;
        private String detail;
    }
}