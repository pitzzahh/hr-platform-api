package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.employee.model.CivilStatus;
import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import dev.araopj.hrplatformapi.employee.model.Gender;
import lombok.experimental.UtilityClass;
import java.util.Arrays;

@UtilityClass
public class EnumUtil {

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & HasCode> E fromCode(Class<?> enumClass, String code) {
        for (var e : enumClass.getEnumConstants()) {
            var hasCode = (HasCode) e;
            if (hasCode.getCode().equals(code)) return (E) e;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    /**
     * Interface to be implemented by enums that have a code.
     * @implNote This interface is used to enforce the presence of the getCode method in enums.
     */
    public interface HasCode {
        String getCode();
    }

    public static void main(String[] args) {
        for (Class<?> c : Arrays.asList(Gender.class, CivilStatus.class, AuditAction.class, EmploymentStatus.class)) {
            System.out.printf("Enum: %s\n", c.getSimpleName());
            for (var e : c.getEnumConstants()) {
                var code = ((HasCode) e).getCode();
                var anEnum = fromCode(c, code);
                System.out.printf("  Code: %s, Enum: %s\n", code, anEnum);
            }
        }
        System.out.println("\nPERM = " + fromCode(EmploymentStatus.class, "PERM"));
    }
}
