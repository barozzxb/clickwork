package vn.clickwork.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "asc";
}
