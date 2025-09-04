package dev.araopj.hrplatformapi.salary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
    private int tranche;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    @Min(value = 1, message = "Salary grade must be at least 1")
    @Max(value = 33, message = "Salary grade must be at most 33")
    private int salaryGrade;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "salaryGrade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalaryData> salaryData;
}
