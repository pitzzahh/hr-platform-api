package dev.araopj.hrplatformapi.salary.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryData extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    private String id;

    @Column(nullable = false)
    @Size(min = 1, max = 8, message = "Salary step must be between 1 and 8")
    private byte step;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "salary_grade_id", nullable = false)
    private SalaryGrade salaryGrade;
}
