package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Salary extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    String id;

    @Column
    double amount;

    @Column
    String currency;

    @OneToOne(mappedBy = "salary", cascade = CascadeType.ALL)
    EmploymentInformation employmentInformation;

}
