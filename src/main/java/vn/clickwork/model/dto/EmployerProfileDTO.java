package vn.clickwork.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmployerProfileDTO {
    private String username;
    private String fullname;
    private String phonenum;
    private String avatar;
    private String website;
    private String taxnumber;
    private String field;
    private String workingdays;
    private String companysize;
    private String sociallink;
    private String overview;

    // Thêm danh sách địa chỉ
    private List<AddressDTO> addresses;

    @Data
    public static class AddressDTO {
        private Long id;
        private String nation;
        private String province;
        private String district;
        private String village;
        private String detail;
    }
}
