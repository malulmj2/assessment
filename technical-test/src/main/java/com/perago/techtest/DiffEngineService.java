/**
 * 
 */
package com.perago.techtest;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.perago.techtest.util.ReflectionUtil;
import com.perago.techtest.util.StringUtils;

/**
 * {@link DiffEngine} Person Implementation.<br>
 * 
 * @author Jabu Msipha
 * 
 */
public class DiffEngineService implements DiffEngine {

    /*
     * (non-Javadoc)
     * 
     * @see com.perago.techtest.DiffEngine#apply(java.io.Serializable,
     * com.perago.techtest.Diff)
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {
	return (T) diff.getRecord().getModifiedValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.perago.techtest.DiffEngine#calculate(java.io.Serializable,
     * java.io.Serializable)
     */
    public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
	DiffRecord record = handleCalculate(original, modified);
	return new Diff<T>(record);
    }

    private <T extends Serializable> DiffRecord handleCalculate(T original, T modified) throws DiffException {
	DiffRecord record = null;
	if (isDeleted(original, modified))
	    record = new DiffRecord(DiffAction.DELETED, original.getClass().getSimpleName(), original, modified);
	else if (isCreated(original, modified))
	    record = calculateForCreate(original, modified);
	else if (isUpdated(original, modified))
	    record = calculateForUpdate(original, modified);

	return record;
    }

    private <T extends Serializable> DiffRecord calculateForCreate(T original, T modified) throws DiffException {
	DiffRecord record;
	DiffAction action = DiffAction.CREATED;
	record = new DiffRecord(action, modified.getClass().getSimpleName(), original, modified);
	List<Field> allFields = ReflectionUtil.getAllFields(modified.getClass(), true);
	for (Field field : allFields) {
	    try {
		field.setAccessible(true);
		record.addRecord(action, field.getName(), null, field.get(modified));
	    } catch (Exception e) {
		throw new DiffException(e.getMessage(), e);
	    }

	}
	return record;
    }

    private <T extends Serializable> DiffRecord calculateForUpdate(T original, T modified) throws DiffException {
	DiffRecord record;
	DiffAction action = DiffAction.UPDATED;
	record = new DiffRecord(action, original.getClass().getSimpleName(), original, modified);
	List<Field> allFields = ReflectionUtil.getAllFields(original.getClass(), true);
	for (Field field : allFields) {
	    try {

		field.setAccessible(true);
		Object originalFieldValue = field.get(original);
		Object modifiedFieldValue = field.get(modified);
		if (!(originalFieldValue == null && modifiedFieldValue == null)) {
		    /*
		     * Information that was not changed must not be reflected in
		     * a Diff
		     */
		    if ((originalFieldValue == null && modifiedFieldValue != null)
			    || (modifiedFieldValue == null && originalFieldValue != null)
			    || (!originalFieldValue.equals(modifiedFieldValue))) {
			if (ReflectionUtil.isCollectionsMapType(field)) {
			    addCollectionsMapTypeRecord(action, field, originalFieldValue, modifiedFieldValue, record);
			} else {
			    DiffRecord subDiffRecord = record.addRecord(action, field.getName(), originalFieldValue,
				    modifiedFieldValue);

			    /*
			     * Diffs must recursively reflect modifications to
			     * all child objects
			     */
			    if (!ReflectionUtil.isJavaType(field) && !ReflectionUtil.isCollectionsMapType(field)) {
				if (originalFieldValue instanceof Serializable
					|| modifiedFieldValue instanceof Serializable) {
				    subDiffRecord.addRecord(handleCalculate((Serializable) originalFieldValue,
					    (Serializable) modifiedFieldValue));
				}
			    }
			}

		    }

		}

	    } catch (Exception e) {
		throw new DiffException(e.getMessage(), e);
	    }

	}
	return record;
    }

    private void addCollectionsMapTypeRecord(DiffAction action, Field field, Object originalFieldValue,
	    Object modifiedFieldValue, DiffRecord record) {
	String originalValue = null;
	String modifiedValue = null;
	if (field.getType().isArray()) {

	    originalValue = convertArrayToString(field, originalFieldValue);
	    modifiedValue = convertArrayToString(field, modifiedFieldValue);
	} else {

	    originalValue = Objects.isNull(originalFieldValue) ? null : originalFieldValue.toString();
	    modifiedValue = Objects.isNull(modifiedFieldValue) ? null : modifiedFieldValue.toString();
	}

	if (!StringUtils.areStringEqual(originalValue, modifiedValue)) {
	    record.addRecord(action, field.getName(), originalValue, modifiedValue);
	}
    }

    private String convertArrayToString(Field field, Object originalFieldValue) {
	String strArray = null;
	if (!Objects.isNull(originalFieldValue) && field.getType().isArray()) {
	    strArray = Arrays.toString((Object[]) originalFieldValue);
	}
	return strArray;
    }

    /**
     * Check if the original object was updated. A non-null original object
     * diffed<br>
     * against non-null modified object must reflect as being “updated” in diff.
     * 
     * @param original
     * @param modified
     * @return - true if parameters are both not null
     */
    private <T extends Serializable> boolean isUpdated(T original, T modified) {
	return original != null || modified != null;
    }

    /**
     * Check if the original object was deleted. A null original object
     * diffed<br>
     * against non-null modified object must reflect as being “created” in diff.
     * 
     * @param original
     * @param modified
     * @return - true if original is null and modified is not null
     */
    private <T> boolean isCreated(T original, T modified) {
	return original == null && modified != null;
    }

    /**
     * Check if the original object was deleted. A non-null original object<br>
     * diffed against null modified object must reflect as being “deleted” in
     * diff
     * 
     * @param original
     * @param modified
     * @return - true if modified is null and original is not null
     */
    private <T> boolean isDeleted(T original, T modified) {
	return original != null && modified == null;
    }

}
