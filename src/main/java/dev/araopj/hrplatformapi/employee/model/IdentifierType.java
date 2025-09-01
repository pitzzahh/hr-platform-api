package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class IdentifierType {
    @Id
    @Uuid
    private String id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., "GSIS", "SSS", "TIN", "PASSPORT"

    @Column(nullable = false)
    private String name; // e.g., "Government Service Insurance System", "Social Security System"

    @Column
    private String country; // optional if you want to scope it by country

    @Column
    private String description;
}
