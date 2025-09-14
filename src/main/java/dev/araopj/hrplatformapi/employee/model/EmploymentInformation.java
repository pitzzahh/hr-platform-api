package dev.araopj.hrplatformapi.employee.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @Column(nullable = false)
    private String sourceOfFund;

    @Column
    private String remarks;

    @OneToOne
    @JoinColumn(name = "employment_information_salary_override_id")
    private EmploymentInformationSalaryOverride employmentInformationSalaryOverride;

    @Column(nullable = false)
    private int step;

    @Column
    private int anticipatedStep;

    @OneToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @OneToOne
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;

    @OneToOne
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;
}
