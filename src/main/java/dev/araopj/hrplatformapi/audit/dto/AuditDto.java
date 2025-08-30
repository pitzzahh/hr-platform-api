package dev.araopj.hrplatformapi.audit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuditDto {

    @NotBlank
    private String entityType;

    @NotNull
    private AuditAction action;

    @NotBlank
    private String entityId;

    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode oldData;

    @NotNull
    private JsonNode newData;

    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode changes;

    @NotBlank
    private String performedBy;
}
