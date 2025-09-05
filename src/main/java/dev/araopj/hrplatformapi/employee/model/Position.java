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
public class Position extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    String id;

    @Column(nullable = false, unique = true)
    String code;

    @Column(nullable = false, unique = true)
    String description;

    @OneToOne(mappedBy = "position", cascade = CascadeType.ALL, optional = false)
    EmploymentInformation employmentInformation;
}
