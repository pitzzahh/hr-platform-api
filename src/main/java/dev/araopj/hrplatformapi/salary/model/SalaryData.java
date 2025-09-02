package dev.araopj.hrplatformapi.salary.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private byte step;

    @Column(nullable = false)
    private double amount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salary_grade_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SalaryGrade salaryGrade;
}
