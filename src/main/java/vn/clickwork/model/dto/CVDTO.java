package vn.clickwork.model.dto;

import lombok.Data;

@Data
public class CVDTO {
    private Long id;
    private String name;
    private String file; // Đường dẫn/URL file PDF
}
