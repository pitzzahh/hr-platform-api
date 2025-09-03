package dev.araopj.hrplatformapi.utils;

import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Custom UUID generator that allows for client-assigned IDs.
 * Checks for an existing ID using a field annotated with @Uuid, or falls back to a default 'getId' method.
 * Generates a new UUID if no ID is assigned or if the ID is null.
 * Used with the @Uuid annotation on entity ID fields to indicate the UUID generation strategy.
 */
@Slf4j
public class UuidGenerator extends SequenceStyleGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner) {
        log.debug("Generating UUID for entity: {}", owner.getClass().getSimpleName());

        if (allowAssignedIdentifiers()) {
            var existingId = getExistingId(owner);
            if (existingId != null) {
                log.debug("Using client-assigned ID: {}", existingId);
                return existingId;
            }
        }

        log.info("Generating new UUID for entity: {}", owner.getClass().getSimpleName());
        return UUID.randomUUID().toString();
    }

    /**
     * Retrieves the existing ID from the entity using the @Uuid annotation or a default 'getId' method.
     *
     * @param owner The entity object.
     * @return The existing ID, or null if none is found.
     * @throws IllegalStateException If the ID getter method cannot be accessed or invoked.
     */
    private String getExistingId(Object owner) {
        if (owner == null) {
            log.warn("Owner entity is null. Cannot retrieve existing ID.");
            return null;
        }

        try {
            // 1. Look for @Uuid annotation
            for (var field : owner.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Uuid.class)) {
                    var annotation = field.getAnnotation(Uuid.class);
                    var methodName = annotation.getterMethod();

                    if (methodName == null || methodName.isBlank()) {
                        log.warn("@Uuid annotation on {} has no valid getterMethod defined.",
                                owner.getClass().getSimpleName());
                        return null;
                    }

                    var value = owner.getClass().getMethod(methodName).invoke(owner);
                    return value != null ? value.toString() : null;
                }
            }

            // 2. Fallback to default getId()
            try {
                var getIdMethod = owner.getClass().getMethod("getId");
                var idValue = getIdMethod.invoke(owner);
                return idValue != null ? idValue.toString() : null;
            } catch (NoSuchMethodException e) {
                log.warn("No getId() method found on {}. Generating new UUID.",
                        owner.getClass().getSimpleName());
                return null;
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(
                    String.format("Failed to access ID getter for entity %s: %s",
                            owner.getClass().getSimpleName(), e.getMessage()), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true; // Allow client-assigned IDs
    }
}