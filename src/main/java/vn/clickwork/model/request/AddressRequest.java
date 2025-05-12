package vn.clickwork.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String nation;
    private String province;
    private String district;
    private String village;
    private String detail;
}
