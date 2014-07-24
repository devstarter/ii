package org.ayfaar.ii.utils;

import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import java.lang.reflect.Field;

public class EntityUtils {
    public static Object getPropertyValue(Object o, String property) {
        try {
            return PropertyUtils.getProperty(o, property);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setPropertyValue(Object o, String property, Object value) {
        try {
            PropertyUtils.setProperty(o, property, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPrimaryKeyValue(Object o) {
        return getPropertyValue(o, getPrimaryKeyFiledName(o.getClass()));
    }

    public static String getPrimaryKeyFiledName(Class c) {
        PrimaryKeyJoinColumn keyJoinColumn = (PrimaryKeyJoinColumn) c.getAnnotation(PrimaryKeyJoinColumn.class);
        if (keyJoinColumn != null) {
            return keyJoinColumn.name().replace("_i", "I");
        }
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getName();
            }
        }
        return null;
    }
}
