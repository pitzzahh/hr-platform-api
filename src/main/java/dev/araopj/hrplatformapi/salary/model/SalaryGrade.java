package dev.araopj.hrplatformapi.salary.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryGrade extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    String id;

    @Column(nullable = false)
    String legalBasis;

    @Column
    byte tranche;

    @Column
    LocalDate effectiveDate;

    @Column(nullable = false)
    @Min(value = 1, message = "Salary grade must be at least 1")
    @Max(value = 33, message = "Salary grade must be at most 33")
    byte salaryGrade;

    @OneToMany(mappedBy = "salaryGrade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SalaryData> salaryData;
}
