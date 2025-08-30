package dev.araopj.hrplatformapi.audit.model;


import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Audit extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    private String id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(nullable = false)
    private String entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private Object oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private Object newData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private Object changes;

    @Column(nullable = false)
    private String performedBy;
}
