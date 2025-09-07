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
public class IdDocumentType extends EntityTimestamp implements Serializable {
    @Id
    @Uuid
    private String id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., "GSIS", "SSS", "TIN", "PASSPORT"

    @Column(nullable = false)
    private String name; // e.g., "Government Service Insurance System", "Social Security System"

    @Column
    private String description;

    @Column(nullable = false)
    private String category; // e.g., "Government", "Private", "Professional"

    @OneToOne(mappedBy = "idDocumentType", cascade = CascadeType.ALL, optional = false)
    IdDocument idDocument;
}
