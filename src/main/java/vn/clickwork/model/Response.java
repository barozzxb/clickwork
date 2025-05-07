package vn.clickwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response {
    private boolean status;
    private String message;
    private Object body;
}
