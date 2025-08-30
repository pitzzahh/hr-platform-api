package dev.araopj.hrplatformapi.utils;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for computing differences between two objects and applying those differences.
 */
@UtilityClass
public class DiffUtil {

    /**
     * Computes the differences between two objects of the same type.
     * Returns a map where keys are field names and values are the new values from newObj.
     *
     * @param oldObj The original object.
     * @param newObj The modified object.
     * @param <T>    The type of the objects.
     * @return A map of field names to their new values.
     * @throws IllegalArgumentException if the objects are of different types or null.
     */
    public static <T> Map<String, Object> diff(T oldObj, T newObj) {
        var changes = new HashMap<String, Object>();

        if (oldObj == null || newObj == null) {
            throw new IllegalArgumentException("Both objects must be non-null");
        }

        var clazz = oldObj.getClass();
        if (!clazz.equals(newObj.getClass())) {
            throw new IllegalArgumentException("Objects must be of the same type");
        }

        for (var field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                var oldVal = field.get(oldObj);
                var newVal = field.get(newObj);

                if (!Objects.equals(oldVal, newVal)) {
                    changes.put(field.getName(), newVal);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not access field: " + field.getName(), e);
            }
        }

        return changes;
    }

    /**
     * Applies the differences from a map to the target object.
     *
     * @param target The object to apply changes to.
     * @param diff   A map of field names to their new values.
     * @param <T>    The type of the target object.
     * @return The modified target object.
     * @throws IllegalArgumentException if the target is null.
     */
    public <T> T applyDiff(T target, Map<String, Object> diff) {
        Class<?> clazz = target.getClass();

        diff.forEach((fieldName, value) -> {
            try {
                var field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException("Could not apply field: " + fieldName, e);
            }
        });

        return target;
    }
}

