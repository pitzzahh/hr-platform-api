package dev.araopj.hrplatformapi.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.util.UUID;

/**
 * Custom UUID generator that allows for client-assigned IDs.
 * If no ID is assigned, it generates a new UUID.
 * Assumes the entity has a getId() method to check for existing IDs.
 * This generator is used with the @Uuid annotation on entity properties to indicate UUID generation strategy.
 */
@Slf4j
public class UuidGenerator extends SequenceStyleGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner) {
        log.debug("Creating UUID if none is assigned");
        try {
            if (this.allowAssignedIdentifiers()) {
                var getIdMethod = owner.getClass().getMethod("getId"); // Assumes the entity has a getId() method
                Object existingId = getIdMethod.invoke(owner);
                if (existingId != null) {
                    return existingId;
                }
            }
        } catch (Exception e) {
            log.warn("Could not access getId method on owner: {}", e.getMessage());
        }
        log.info("Generating new UUID, as none was assigned");
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true; // REQUIRED to allow client-assigned IDs
    }
}