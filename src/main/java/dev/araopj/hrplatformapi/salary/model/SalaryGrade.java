package dev.araopj.hrplatformapi.salary.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryGrade extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    private String id;

    @Column(nullable = false)
    private String legalBasis;

    @Column
    private byte tranche;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    @Min(value = 1, message = "Salary grade must be at least 1")
    @Max(value = 33, message = "Salary grade must be at most 33")
    private byte salaryGrade;
}
