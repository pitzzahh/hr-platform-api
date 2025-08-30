package dev.araopj.hrplatformapi.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false)
    String legalBasis;

    @Column
    byte tranche;

    @Column
    LocalDate effectiveDate;

    @Column(nullable = false)
    @Size(min = 1, max = 33, message = "Salary grade must be between 1 and 33")
    byte salaryGrade;

    @OneToMany(mappedBy = "salaryGrade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SalaryData> salaryData;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
