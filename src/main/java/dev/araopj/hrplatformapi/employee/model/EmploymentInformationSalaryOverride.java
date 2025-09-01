package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmploymentInformationSalaryOverride extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    private String id;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @OneToOne(mappedBy = "employmentInformationSalaryOverride", cascade = CascadeType.ALL)
    EmploymentInformation employmentInformation;
}
