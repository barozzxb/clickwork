package vn.clickwork.model.request;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorUsername;
    private String title;
    private String content;
}
