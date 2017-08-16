/**
 * 
 */
package com.perago.techtest.util;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jabu
 *
 */
public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * Gets all the fields and the inherited one for the provided class
     * 
     * @param clazz
     * @param excludeSerialVersionUID
     *            - Flag to ignore field if is name is serialVersionUID
     * @return
     */
    public static List<Field> getAllFields(Class<?> clazz, boolean excludeSerialVersionUID) {
	List<Field> allFields = getAllFieldsRec(clazz, new ArrayList<Field>());
	if (excludeSerialVersionUID) {
	    allFields = allFields.stream().filter(field -> {

		return !"serialVersionUID".equals(field.getName());
	    }).collect(toList());
	}

	return allFields;
    }

    private static List<Field> getAllFieldsRec(Class<?> clazz, List<Field> vector) {
	Class<?> superClazz = clazz.getSuperclass();
	if (superClazz != null) {
	    getAllFieldsRec(superClazz, vector);
	}
	vector.addAll(Arrays.asList(clazz.getDeclaredFields()));
	return vector;
    }

    /**
     * Check if the field type is java defined type.<br>
     * e.g primitives, enum, String, Number
     */
    public static boolean isJavaType(Field field) {
	if (!Objects.isNull(field)) {
	    if (Number.class.equals(field.getType().getSuperclass()))
		return true;
	    else if (field.getType().isPrimitive())
		return true;
	    else if (field.getType().equals(Boolean.class))
		return true;
	    else if (field.getType().isEnum())
		return true;
	    else if (field.getType().equals(String.class))
		return true;
	    else if (field.getType().equals(Character.class))
		return true;
	}
	return false;
    }

    /**
     * Check is the field has its type defines as a collection type, array or
     * Map
     */
    public static boolean isCollectionsMapType(Field field) {
	if (!Objects.isNull(field)) {
	    if (Collection.class.isAssignableFrom(field.getType()))
		return true;
	    else if (Map.class.isAssignableFrom(field.getType()))
		return true;
	    else if (field.getType().isArray())
		return true;
	}
	return false;
    }
}
