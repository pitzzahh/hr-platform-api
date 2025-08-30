package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "station_org")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DivisionStationPlaceOfAssignment extends EntityTimestamp implements Serializable {
    @Id
    @Uuid
    String id;

    @Column(nullable = false)
    String code;

    @Column(nullable = false, unique = true)
    String name;

    @Column
    String shortName;
}
