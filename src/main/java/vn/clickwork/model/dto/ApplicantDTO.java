package vn.clickwork.model.dto;

import lombok.Data;

@Data
public class ApplicantDTO {
    private Long id;
    private String fullname;
    private String email;
    private String phone;
    private CVDTO defaultCV;
    // Thêm các trường cần thiết khác, KHÔNG nên trả về danh sách liên kết hoặc
    // thông tin nhạy cảm
}
