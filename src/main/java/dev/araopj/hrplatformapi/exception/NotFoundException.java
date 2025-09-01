package dev.araopj.hrplatformapi.exception;

import dev.araopj.hrplatformapi.utils.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom exception thrown when a requested entity is not found in the system.
 * Provides detailed messages based on the entity type and identifiers involved.
 * Example usage:
 * <pre>
 * throw new NotFoundException("123", NotFoundException.EntityType.EMPLOYEE);
 * throw new NotFoundException("123", "456", NotFoundException.EntityType.EMPLOYEE, "departmentId");
 * </pre>
 * The exception message will indicate which entity type and IDs were not found.
 * The EntityType enum includes a description for each entity type to enhance the clarity of the exception message.
 *
 * @see EnumUtil.HasCode
 * @see EntityType
 * @see RuntimeException
 * @since 0.0.1
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String id, EntityType entityType) {
        super("%s record with ID %s not found.".formatted(entityType.getDescription(), id));
    }

    public NotFoundException(String id, String otherId, EntityType entityType, String otherIdFieldName) {
        super("%s record with id %s and %s %s not found.".formatted(entityType.getDescription(), id, otherId, otherIdFieldName));
    }

    @Getter
    @AllArgsConstructor
    public enum EntityType implements EnumUtil.HasCode {
        EMPLOYEE("EMPLOYEE", "Employee"),
        GSIS("GSIS", "Government Service Insurance System"),
        DIVISION_STATION_PLACE_OF_ASSIGNMENT("DIVISION_STATION_PLACE_OF_ASSIGNMENT", "Division Station Place Of Assignment"),
        EMPLOYMENT_INFORMATION("EMPLOYMENT_INFORMATION", "Employment Information"),
        EMPLOYMENT_INFORMATION_SALARY_OVERRIDE("EMPLOYMENT_INFORMATION_SALARY_OVERRIDE", "Employment Information Salary Override"),
        POSITION("POSITION", "Position"),
        AUDIT("AUDIT", "Audit"),
        USER("USER", "User"),
        SALARY_DATA("SALARY_DATA", "Salary Data"),
        SALARY_GRADE("SALARY_GRADE", "Salary Grade");

        private final String code;
        private final String description;
    }

}
