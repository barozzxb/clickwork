package vn.clickwork.model.request;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AppointmentRequest {
    private Timestamp time;
    private String place;
    private String website;
}
