package vn.clickwork.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @JsonManagedReference
    private Applicant applicant;
    
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    
    private Timestamp savedAt;
}