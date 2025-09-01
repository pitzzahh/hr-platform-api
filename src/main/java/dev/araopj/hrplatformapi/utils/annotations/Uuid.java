package dev.araopj.hrplatformapi.utils.annotations;

import dev.araopj.hrplatformapi.utils.UuidGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Annotation to specify that a field should use the UuidGenerator for ID generation.
 * This allows for client-assigned UUIDs, generating a new UUID only if none is provided
 */
@IdGeneratorType(UuidGenerator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Uuid {
}
