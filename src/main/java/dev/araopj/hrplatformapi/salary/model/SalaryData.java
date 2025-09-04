package dev.araopj.hrplatformapi.salary.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Min(value = 1, message = "Salary step must be at least 1")
    @Max(value = 8, message = "Salary step must be at most 8")
    private int step;

    @Column(nullable = false)
    private double amount;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "salary_grade_id", nullable = false)
    private SalaryGrade salaryGrade;
}
