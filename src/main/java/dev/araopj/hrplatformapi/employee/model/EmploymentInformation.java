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
public class EmploymentInformation extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EmploymentStatus employmentStatus;

    @Column(nullable = false)
    String sourceOfFund;

    @Column
    String remarks;

    @OneToOne
    @JoinColumn(name = "employment_information_salary_override_id")
    EmploymentInformationSalaryOverride employmentInformationSalaryOverride;

    @Column(nullable = false)
    int step;

    @Column
    int anticipatedStep;

    @OneToOne
    @JoinColumn(name = "position_id", nullable = false)
    Position position;

    @OneToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    Workplace workplace;
}
