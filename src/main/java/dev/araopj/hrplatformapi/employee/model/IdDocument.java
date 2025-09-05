package dev.araopj.hrplatformapi.employee.model;

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
public class IdDocument extends EntityTimestamp implements Serializable {
    @Id
    @Uuid
    private String id;

    @Column(nullable = false, unique = true)
    private String identifierNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_document_type_id")
    private IdDocumentType idDocumentType;

    @Column
    private LocalDate issuedDate;

    @Column
    private String issuedPlace;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}

