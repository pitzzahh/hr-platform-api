package dev.araopj.hrplatformapi.audit.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Audit extends EntityTimestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false)
    String entityType;

    @Column(nullable = false)
    AuditAction action;

    @Column(nullable = false)
    String entity_id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    Object oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    Object newData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    Object changes;

    @Column(nullable = false)
    String performedBy;
}
