package vn.clickwork.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {
    private Long id;
    private String nation;
    private String province;
    private String district;
    private String village;
    private String detail;

    // Phương thức tiện ích để lấy địa chỉ đầy đủ
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (detail != null && !detail.isEmpty()) {
            sb.append(detail);
        }
        if (village != null && !village.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(village);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province);
        }
        if (nation != null && !nation.isEmpty() && !nation.equalsIgnoreCase("Việt Nam")) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(nation);
        }
        return sb.toString();
    }
}
