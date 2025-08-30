package dev.araopj.hrplatformapi.audit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Audit {
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

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
