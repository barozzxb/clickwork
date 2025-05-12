package vn.clickwork.model.request;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    private String role;
    private String status;
}