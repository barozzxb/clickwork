package vn.clickwork.model.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import vn.clickwork.enumeration.EResponseStatus;

@Getter
@Setter
public class SupportResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Timestamp sendat;
    private EResponseStatus status;
    private String response;
    private Long applicantId;
    private String applicantEmail;
    private Long employerId;
    private String employerEmail;
    private String adminUsername;
}