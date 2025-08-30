package dev.araopj.hrplatformapi.utils;

import lombok.experimental.UtilityClass;

/**
 * Utility class for merging non-null fields from one object into another.
 * This class uses reflection to inspect and copy fields.
 */
@UtilityClass
public class MergeUtil {
    /**
     * Merges non-null fields from newObj into oldObj.
     * Only fields that are non-null in newObj will overwrite the corresponding fields in oldObj.
     *
     * @param oldObj The original object to be updated.
     * @param newObj The object containing new values.
     * @param <T>    The type of the objects being merged.
     * @return The updated oldObj with merged values.
     * @throws IllegalArgumentException if either object is null or if they are of different types.
     */
    public static <T> T merge(T oldObj, T newObj) {
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
                var newVal = field.get(newObj);
                if (newVal != null) {
                    field.set(oldObj, newVal); // overwrite only if newVal is not null
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not access field: " + field.getName(), e);
            }
        }

        return oldObj;
    }
}

