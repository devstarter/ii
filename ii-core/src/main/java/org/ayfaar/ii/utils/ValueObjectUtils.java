package org.ayfaar.ii.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.jetbrains.annotations.Nullable;
import org.springframework.ui.ModelMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ValueObjectUtils {

    /**
     * use getModelMap instead
     *
     * @throws Exception
     */
    @Deprecated
    public static Collection<ModelMap> convertToPlainObjects(Collection<?> list) {
        return (Collection<ModelMap>) getModelMap(list);
    }

    public static <E> Collection<ModelMap> convertToPlainObjects(Collection<E> list, Modifier<E> modifier) {
        return convertToPlainObjects(list, true, modifier);
    }
    public static <E> Collection<ModelMap> convertToPlainObjects(Collection<E> list, Boolean avoidTableAdmin, Modifier<E> modifier) {
        Collection<ModelMap> result = new ArrayList<ModelMap>();

        for (E entity : list) {
            ModelMap map = (ModelMap) ValueObjectUtils.getModelMap(entity, avoidTableAdmin);
            if (modifier != null) {
                modifier.modify(entity, map);
            }
            result.add(map);
        }

        return result;
    }

    public static String[] excludeAnnotations = {
        "javax.persistence.ManyToMany",
        "javax.persistence.ManyToOne",
        "javax.persistence.OneToMany",
        "javax.persistence.OneToOne"
    };

    public static Object getModelMap(@Nullable final Object entity, String ...keepProperties) {
        return getModelMap(entity, null, keepProperties.length > 0, keepProperties);
    }

    public static Object getModelMap(@Nullable final Object entity, Boolean avoidTableAdmin, String ...keepProperties) {
        return getModelMap(entity, null, avoidTableAdmin, keepProperties);
    }

    protected static Object getModelMap(@Nullable final Object entity,
                                        @Nullable TableAdminType type,
                                        Boolean avoidTableAdmin,
                                        @Nullable String ...keepProperties) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Number) {
            return entity;
        }

        if (entity instanceof Collection) {
            List result = new ArrayList();
            Collection collection = (Collection) entity;
            if (collection instanceof PersistentBag && !((PersistentBag) collection).wasInitialized()) {
                return null;
            }
            for (Object o : collection) {
                result.add(getModelMap(o, type, avoidTableAdmin, keepProperties));
            }
            return result;
        }

        if (entity instanceof Object[]) {
            Object[] array = (Object[]) entity;
            Object[] result = new Object[array.length];
            for (int i=0; i<array.length; i++) {
                result[i] = getModelMap(array[i], type, avoidTableAdmin, keepProperties);
            }
            return result;
        }

        if (entity.getClass().equals(ModelMap.class)) {
            return entity;
        }

        ModelMap map = new ModelMap();

        if (type != null && Types.FK.equals(type.value())) {
//            if (type.key() != "") map.put("key", getProperty(entity, type.key()));
//            if (type.name() != "") map.put("value", getProperty(entity, type.name()));
//            return map;
            String keyField = type.keyField();
            if (keyField == null || keyField.isEmpty()) {
                keyField = EntityUtils.getPrimaryKeyFiledName(entity.getClass());
            }
            return getProperty(entity, keyField);
        }

        Field[] declaredFields = entity.getClass().getDeclaredFields();
        if (entity.getClass().getSuperclass() != null) {
            declaredFields = ArrayUtils.addAll(declaredFields, entity.getClass().getSuperclass().getDeclaredFields());
        }

        for (final Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            if ("this$0".equals(fieldName)) continue;

            Object value = null;

            TableAdminType fieldTypeAnnotation = !avoidTableAdmin ? field.getAnnotation(TableAdminType.class) : null;

            if (keepField(fieldName, keepProperties) || (fieldTypeAnnotation != null && Types.FK.equals(fieldTypeAnnotation.value()))) {
                value = getProperty(entity, field.getName());
//                if (value != null) {
                    value = getModelMap(value, fieldTypeAnnotation, avoidTableAdmin, getNestedPropertiesChine(keepProperties));
                    if (fieldTypeAnnotation != null && Types.FK.equals(fieldTypeAnnotation.value())) {
                        fieldName += "_fk";
                    }
//                }
            } else {
                Boolean avoidThisField = false;
                for (Annotation annotation : field.getAnnotations()) {
                    if (Arrays.asList(excludeAnnotations).contains(annotation.annotationType().getName())) {
                        avoidThisField = true;
                    }
                    if (annotation instanceof ViewAsJoinedString) {
                        avoidThisField = false;

                        ViewAsJoinedString a = (ViewAsJoinedString) annotation;
                        value = getProperty(entity, field.getName());
                        Collection collection = (Collection) value;

                        String stringValue = "";
                        for (Object item : collection) {
                            stringValue += getProperty(item, a.joinField());
                            stringValue += a.joinBy();
                        }
                        if (stringValue.length() > 0) {
                            stringValue = stringValue.substring(0, stringValue.lastIndexOf(a.joinBy()));
                        }
                        value = stringValue;
                        fieldName = a.modelField();
                        break;
                    }
                }

                if (avoidThisField) continue;
            }

            // to avoid calling getters before it needed
            if (value == null && !fieldName.matches(".+_fk$")) {
                value = getProperty(entity, fieldName);
            }

            map.put(fieldName, value);
        }

        return map;
    }

    private static String[] getNestedPropertiesChine(String[] keepProperties) {
        List<String> pps = new ArrayList<String>();
        for (String propertyChain : keepProperties) {
            int dotIndex = propertyChain.indexOf(".");
            if (dotIndex > 0) {
                pps.add(propertyChain.substring(dotIndex+1));
            }
        }
        return pps.toArray(new String[pps.size()]);
    }

    private static boolean keepField(String fieldName, @Nullable String[] keepProperties) {
        if (keepProperties != null) {
            for (String propertyChain : keepProperties) {
                String firstProperty = propertyChain.indexOf(".") > 0
                        ? propertyChain.substring(0, propertyChain.indexOf("."))
                        : propertyChain;

                if (fieldName.equals(firstProperty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static interface Modifier<E> {
        void modify(E entity, ModelMap map);
    }

    public static Object getProperty(Object bean, String name) {
        if ("this$0".equals(name)) return null;
        try {
            Field field = null;
            for (Field f : bean.getClass().getFields()) {
                if (f.getName().equals(name)) {
                    field = f;
                }
            }
            return (field != null) ? field.get(bean) : PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
