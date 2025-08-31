package dev.araopj.hrplatformapi.audit.model;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuditAction implements EnumUtil.HasCode {

    CREATE("C", "Create operation"),
    UPDATE("U", "Update operation"),
    DELETE("D", "Delete operation"),
    VIEW("V", "View operation"),
    ERROR("E", "Error operation");

    private final String code;
    private final String description;

}
