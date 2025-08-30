package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
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

    @ManyToOne
    @JoinColumn(name = "employment_information_salary_override_id")
    EmploymentInformationSalaryOverride salaryOverride;

    @Column(nullable = false)
    @Size(min = 1, max = 8, message = "Salary step must be between 1 and 8")
    byte step;

    @Column
    @Size(min = 1, max = 8, message = "Anticipated step must be between 1 and 8")
    byte anticipatedStep;

    @ManyToOne(optional = false)
    @JoinColumn(name = "position_id")
    Position position;

    @ManyToOne
    @JoinColumn(name = "station_org_id")
    DivisionStationPlaceOfAssignment divisionStationPlaceOfAssignment;
}
