package dev.araopj.hrplatformapi.audit.model;

import com.fasterxml.jackson.databind.JsonNode;
import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

/**
 * Entity representing an audit record in the HR platform.
 * <p>
 * <b>IMPORTANT: FOR REMOVAL</b> in favor of better auditing solutions like <b>Zipkin</b> or <b>OpenTelemetry</b>.
 * </p>
 * This entity captures audit information for actions performed on various entities,
 * including the type of action, the entity affected, old and new data states, and
 * metadata about who performed the action and when.
 *
 * @see AuditAction
 * @see JsonNode
 */
@Deprecated
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
    @Column(columnDefinition = "json")
    private JsonNode oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private JsonNode newData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private JsonNode changes;

    @Column(nullable = false)
    private String performedBy;
}
