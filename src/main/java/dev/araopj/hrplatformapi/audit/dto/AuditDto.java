package dev.araopj.hrplatformapi.audit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Object oldData;

    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object newData;

    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object changes;

    @NotBlank
    private String performedBy;
}
