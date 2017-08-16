/**
 * 
 */
package com.perago.techtest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jabu
 * 
 */
public class DiffRecord {
    private DiffAction action;
    private String fieldName;
    private Object originalValue;
    private Object modifiedValue;
    private List<DiffRecord> records = new ArrayList<DiffRecord>();

    public DiffRecord(DiffAction action, String fieldName, Object originalValue, Object modifiedValue) {
	super();
	this.action = action;
	this.fieldName = fieldName;
	this.originalValue = originalValue;
	this.modifiedValue = modifiedValue;
    }

    public DiffAction getAction() {
	return action;
    }

    public String getFieldName() {
	return fieldName;
    }

    public Object getOriginalValue() {
	return originalValue;
    }

    public Object getModifiedValue() {
	return modifiedValue;
    }

    public List<DiffRecord> getRecords() {
	return records;
    }

    public DiffRecord addRecord(DiffAction action, String fieldName, Object originalValue, Object modifiedValue) {
	DiffRecord diffRecord = new DiffRecord(action, fieldName, originalValue, modifiedValue);
	addRecord(diffRecord);
	return diffRecord;
    }

    public boolean addRecord(DiffRecord diffRecord) {
	return records.add(diffRecord);
    }

    @Override
    public String toString() {
	return "DiffItem [action=" + action + ", fieldName=" + fieldName + ", originalValue=" + originalValue + ", newValue=" + modifiedValue + ", records=" + records + "]";
    }

}
