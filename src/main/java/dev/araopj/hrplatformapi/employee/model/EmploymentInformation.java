package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    EmploymentStatus employmentStatus;

    @Column(nullable = false)
    String sourceOfFund;

    @Column
    String remarks;

    @OneToOne
    @JoinColumn(name = "employment_information_salary_override_id", unique = true)
    EmploymentInformationSalaryOverride employmentInformationSalaryOverride;

    @Column(nullable = false)
    @Size(min = 1, max = 8, message = "Salary step must be between 1 and 8")
    int step;

    @Column
    @Size(min = 1, max = 8, message = "Anticipated step must be between 1 and 8")
    int anticipatedStep;

    @OneToOne
    @JoinColumn(name = "position_id", nullable = false)
    Position position;

    @OneToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    Workplace workplace;
}
