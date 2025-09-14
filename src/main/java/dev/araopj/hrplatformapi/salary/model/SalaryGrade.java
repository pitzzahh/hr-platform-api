package dev.araopj.hrplatformapi.salary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity representing a Salary Grade in the HR platform.
 * <p>
 * <b>IMPORTANT: DEPRECATED</b> To be moved to a microservice with {@link SalaryData}
 * </p>
 * This entity captures the details of salary grades, including legal basis,
 * tranche, effective date, and associated salary data. It extends {@link EntityTimestamp}
 * to include automatic timestamping for creation and updates.
 *
 * @deprecated This entity is deprecated and may be removed in future versions.
 */
@Deprecated
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
    private int salaryGrade;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "salaryGrade", cascade = CascadeType.ALL)
    private List<SalaryData> salaryData;
}
