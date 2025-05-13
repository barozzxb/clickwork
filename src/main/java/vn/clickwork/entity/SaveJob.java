package vn.clickwork.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "saved_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveJob implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Thêm JsonBackReference để tránh vòng lặp vô hạn
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;
    
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    
    private Timestamp savedAt;
}