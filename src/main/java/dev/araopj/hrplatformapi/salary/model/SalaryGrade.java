package dev.araopj.hrplatformapi.salary.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 33, message = "Salary grade must be between 1 and 33")
    byte salaryGrade;

    @OneToMany(mappedBy = "salaryGrade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SalaryData> salaryData;
}
