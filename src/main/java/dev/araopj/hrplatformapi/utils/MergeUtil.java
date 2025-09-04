package dev.araopj.hrplatformapi.utils;

import org.springframework.beans.BeanWrapperImpl;

/**
 * Utility class for merging non-null fields from one object into another.
 * This class uses reflection to inspect and copy fields.
 */
public class MergeUtil {

    /**
     * Merges non-null fields from newObj into oldObj.
     *
     * @param oldObj The original object to be updated.
     * @param newObj The object containing new values.
     * @param <T>    The type of the objects being merged.
     * @return The updated oldObj with non-null fields from newObj.
     */
    public static <T> T merge(T oldObj, T newObj) {
        var oldWrap = new BeanWrapperImpl(oldObj);
        var newWrap = new BeanWrapperImpl(newObj);

        for (var pd : newWrap.getPropertyDescriptors()) {
            var newVal = newWrap.getPropertyValue(pd.getName());
            if (newVal != null && oldWrap.isWritableProperty(pd.getName())) {
                oldWrap.setPropertyValue(pd.getName(), newVal);
            }
        }
        return oldObj;
    }
}

