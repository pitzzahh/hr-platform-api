package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DivisionStationPlaceOfAssignment extends EntityTimestamp implements Serializable {
    @Id
    @Uuid
    private String id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String shortName;

    @OneToOne(mappedBy = "divisionStationPlaceOfAssignment", cascade = CascadeType.ALL)
    private EmploymentInformation employmentInformation;
}
