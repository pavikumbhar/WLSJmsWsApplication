package com.pavikumbhar.javaheart.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of useful methods to work with reflection and annotation
 * 
 *
 *
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Return a list of Field objects reflecting all the fields declared by the Class object. This
     * includes public, protected, default (package) access, and private fields, includes inherited
     * fields. This method returns null if the class or interface declares no fields.
     * 
     * @param clazz
     *            where will try to get the fields
     * @return a list of field objects just if contain at least one element, otherwise return null
     * @see {@link #getFieldsInClass(Class)} {@link #getFieldsInSuperclass(Class)}
     */
    public static final List<Field> getFields(final Class<?> clazz) {
        if (clazz == null) return null;

        // Get fields in class
        List<Field> fields = getFieldsInClass(clazz);
        if (fields == null) fields = new ArrayList<Field>();
        // Add all fields in superclass
        fields.addAll(getFieldsInSuperclass(clazz));

        return fields.isEmpty() ? null : fields;
    }

    /**
     * Return a list of Field objects reflecting all the fields declared by the Class object. This
     * includes public, protected, default (package) access, and private fields, not includes
     * inherited fields. This method returns null if the class or interface declares no fields.
     * 
     * @param clazz
     *            where will try to get the fields
     * @return a list of field objects just if contain at least one element, otherwise return null
     * @see {@link #getFields(Class)} {@link #getFieldsInSuperclass(Class)}
     */
    public static final List<Field> getFieldsInClass(final Class<?> clazz) {
        if (clazz == null) return null;

        // Get fields in class
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }

        return fields.isEmpty() ? null : fields;
    }

    /**
     * Return a list of Field objects reflecting all the fields declared by the superclass object.
     * This includes public, protected, default (package) access, and private fields, includes
     * inherited fields. This method returns null if the class or interface declares no fields.
     * 
     * @param clazz
     *            where will try to get the fields
     * @return a list of field objects just if contain at least one element, otherwise return null
     * @see {@link #getFields(Class)} {@link #getFieldsInClass(Class)}
     */
    public static final List<Field> getFieldsInSuperclass(final Class<?> clazz) {
        if (clazz == null) return null;

        // Get superclass
        Class<?> superClass = clazz.getSuperclass();
        List<Field> fieldsSuperclass = new ArrayList<Field>();

        // Recursive loop to get field in superclass
        while (superClass != Object.class) {
            List<Field> fields = new ArrayList<Field>();
            for (Field field : superClass.getDeclaredFields()) {
                fields.add(field);
            }

            if (fields != null && !fields.isEmpty()) {
                fieldsSuperclass.addAll(fields);
            }
            superClass = superClass.getSuperclass();
        }

        return fieldsSuperclass;
    }

    /**
     * An convenience method, equivalent to {@link #getFieldsWithAnyAnnotations(Class, Class...)} <p/>
     * Return a list of Field objects reflecting any fields that has at least one annotation class
     * declared by the Class object. This includes public, protected, default (package) access, and
     * private fields, includes inherited fields. This method returns null if the class or interface
     * declares no fields.
     */
    public static final <T extends Annotation> List<Field> getFieldsWithAnnotations(Class<?> clazz,
            Class<?>... annotationClasses) {
        return getFieldsWithAnyAnnotations(clazz, annotationClasses);
    }

    /**
     * @see {@link #getFieldsWithAnnotations(Class, Class...)}
     */
    public static final <T extends Annotation> List<Field> getFieldsWithAnyAnnotations(Class<?> clazz,
            Class... annotationClasses) {
        // Get fields of class
        List<Field> fields = getFields(clazz);
        if (fields == null || fields.isEmpty()) return null;

        // Checking for fields with annotations
        List<Field> fieldsWithAnnotation = new ArrayList<Field>();
        for (Field field : fields) {
            if (hasAnyAnnotations(field, annotationClasses)) {
                fieldsWithAnnotation.add(field);
            }
        }

        return fieldsWithAnnotation.isEmpty() ? null : fieldsWithAnnotation;
    }

    /**
     * @see {@link #getFieldsWithAnnotations(Class, Class...)}
     */
    public static final <T extends Annotation> List<Field> getFieldsWithAllAnnotations(Class<?> clazz,
            Class<T>... annotationClasses) {
        // Get fields of class
        List<Field> fields = getFields(clazz);
        if (fields == null || fields.isEmpty()) return null;

        // Checking for fields with annotations
        List<Field> fieldsWithAnnotation = new ArrayList<Field>();
        for (Field field : fields) {
            if (hasAllAnnotations(field, annotationClasses)) {
                fieldsWithAnnotation.add(field);
            }
        }

        return fieldsWithAnnotation.isEmpty() ? null : fieldsWithAnnotation;
    }

    /**
     * An convenience method, equivalent to {@link #getFieldByName(Class, String)}
     */
    public static final Field getFieldByName(Object obj, String fieldName) {
        return obj != null ? getFieldByName(obj.getClass(), fieldName) : null;
    }

    /**
     * Return a field objects of the class if and only if the field name represents the same
     * sequence of characters as the specified String
     * 
     * @param clazz
     * @param fieldName
     *            corresponding to the specified String
     * @return Return field object if and only if the field name is equal to the specified String,
     *         otherwise return null
     * @see {@link #getFields(Class)}
     */
    public static final Field getFieldByName(Class<?> clazz, String fieldName) {
        // Checking for filed name
        if (fieldName == null || fieldName.length() == 0) return null;

        // Get fields list of class
        List<Field> fields = getFields(clazz);
        if (fields == null || fields.isEmpty()) return null;

        // Search by field name
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) return field;
        }

        // Not found
        return null;
    }

    /**
     * Return true if field has at least one annotation class, otherwise return false
     * @param field
     * @param classes
     *            corresponding to the annotation class
     * @return true if field has at least one annotation class, otherwise return false
     * @see {@link #hasAnyAnnotations(Field, Class...)} {@link #hasAllAnnotations(Field, Class...)}
     */
    public static final <T extends Annotation> boolean hasAnnotations(Field field, Class<?>... classes) {
        return hasAnyAnnotations(field, classes);
    }

    /**
     * @see {@link #hasAnnotations(Field, Class...)}
     */
    public static final <T extends Annotation> boolean hasAnyAnnotations(Field field, Class... classes) {
        if (classes == null || classes.length == 0) return false;

        for (Class<T> clazz : classes) {
            if (field.isAnnotationPresent(clazz)) return true;
        }

        return false;
    }

    /**
     * @see {@link #hasAnnotations(Field, Class...)}
     */
    public static final <T extends Annotation> boolean hasAllAnnotations(Field field, Class... classes) {
        if (classes == null || classes.length == 0) return false;

        for (Class<T> clazz : classes) {
            if (!field.isAnnotationPresent(clazz)) return false;
        }

        return true;
    }

    /**
     * An convenience method, equivalent to {@link #hasAnnotationClass(Class, Class)}
     */
    public static final <T extends Annotation> boolean hasAnnotationClass(Object object, Class<T> annotationClass) {
        return object != null ? hasAnnotationClass(object.getClass(), annotationClass) : null;
    }

    /**
     * Return true if class object has a specific annotation class, otherwise return false
     * 
     * @param entityClass
     * @param annotationClass
     * @return Return true if class object has a specific annotation class, otherwise return false
     * @see {@link #hasAnnotation(Field, Class)}
     */
    public static final <T extends Annotation> boolean hasAnnotationClass(Class<?> entityClass, Class<T> annotationClass) {
        return entityClass != null && entityClass.isAnnotationPresent(annotationClass);
    }

    /**
     * Return a specific annotation class if exist in class, otherwise return false
     * 
     * @param entityClass
     * @param annotationClass
     * @return a specific annotation class if exist in class, otherwise return false
     * @see {@link #hasAnnotationClass(Class, Class)}
     */
    public static final <T extends Annotation> T getAnnotationClass(Class<?> entityClass, Class<T> annotationClass) {
        return hasAnnotationClass(entityClass, annotationClass) ? entityClass.getAnnotation(annotationClass) : null;
    }

    /**
     * Return the field value as a Object
     * 
     * @param object
     * @param field
     * @return the field value as a Object
     * @see {@link #getValue(Object, Field, Class)} {@link java.lang.reflect.Field#get(Object)}
     */
    public static final Object getValue(final Object object, final Field field) {
        // Checking for objects
        if (field == null || object == null) return null;

        try {
            // Suppress java language access
            field.setAccessible(true);
            // Get the value
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return the field value as a specific type
     * 
     * @param object
     * @param field
     * @param clazz
     *            corresponding to the specific type
     * @return the field value as a specific type
     * @see {@link #getValue(Object, Field)} {@link java.lang.reflect.Field#get(Object)}
     */
    public static final <T> T getValue(final Object object, final Field field, final Class<T> clazz) {
        Object value = getValue(object, field);
        return value != null && value.getClass().equals(clazz) ? clazz.cast(value) : null;
    }

    public static final void setValue(final Object obj, final Object value, final Field field) {
        // Checking for objects
        if (field == null || obj == null) return;

        try {
            // Get the old value
            Object oldValue = getValue(obj, field);
            // If is the same value
            if (oldValue == value) return;
            // Set the new value
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return true if the object is a instance of collection, otherwise return false
     * 
     * @param object
     * @return true if the object is a instance of collection, otherwise return false
     */
    public static final boolean isFieldIsCollection(Object object) {
        return ((object instanceof java.util.AbstractList) || (object instanceof java.util.AbstractSet));
    }

    /**
     * Return true if the object is a array, otherwise return false
     * 
     * @param object
     * @return true if the object is a array, otherwise return false
     */
    public static final boolean isFieldIsArray(Object object) {
        return object != null && object.getClass().getComponentType() != null;
    }

}
