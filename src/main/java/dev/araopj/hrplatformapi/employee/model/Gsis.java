package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
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
public class Gsis extends EntityTimestamp implements Serializable {
    @Id
    @Uuid
    String id;

    @Column(nullable = false, name = "business_partner_number", unique = true)
    String businessPartnerNumber;

    @Column
    LocalDate issuedDate;

    @Column
    String issuedPlace;

    @OneToOne(mappedBy = "gsis", cascade = CascadeType.ALL)
    Employee employee;
}
