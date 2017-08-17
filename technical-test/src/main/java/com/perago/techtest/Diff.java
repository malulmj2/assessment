package com.perago.techtest;

import java.io.Serializable;

import com.perago.techtest.impl.DiffRecord;

/**
 * The object representing a diff. Implement this class as you see fit.
 * 
 */
public class Diff<T extends Serializable> {
	private DiffRecord record;
	
	public Diff(DiffRecord record) {
		super();
		this.record =record;
	}
	public DiffRecord getRecord() {
		return record;
	}
}
