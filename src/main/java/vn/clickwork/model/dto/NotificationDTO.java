package vn.clickwork.model.dto;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Timestamp sendAt;
    private boolean read;
}
