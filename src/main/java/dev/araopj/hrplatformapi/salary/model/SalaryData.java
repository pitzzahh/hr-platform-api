package dev.araopj.hrplatformapi.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "salary_data")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false)
    @Size(min = 1, max = 8, message = "Salary step must be between 1 and 8")
    byte step;

    @Column(nullable = false)
    Double amount;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime created_at;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updated_at;
}
