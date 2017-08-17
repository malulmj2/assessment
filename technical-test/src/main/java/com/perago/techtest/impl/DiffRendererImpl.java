/**
 * 
 */
package com.perago.techtest.impl;

import com.perago.techtest.Diff;
import com.perago.techtest.DiffException;
import com.perago.techtest.DiffRenderer;

/**
 * @author Jabu
 *
 */
public class DiffRendererImpl implements DiffRenderer {

    @Override
    public String render(Diff<?> diff) throws DiffException {
	StringBuilder display = new StringBuilder(50);
	DiffRecord record = diff.getRecord();
	renderDiffRecord(display, record, String.valueOf(1));
	return display.toString();
    }

    private void renderDiffRecord(StringBuilder display, DiffRecord record, String displayNumber) {
	display.append(displayNumber);
	switch (record.getAction()) {
	case CREATED:
	    display.append(" Create: ").append(record.getFieldName());
	    // If not a parent that contains other fields/records
	    if (record.getRecords().isEmpty())
		display.append(" as \"").append(record.getModifiedValue()).append("\"");
	    break;
	case UPDATED:
	    display.append(" Update: ").append(record.getFieldName());
	    // If not a parent that contains other fields/records
	    if (record.getRecords().isEmpty()) {
		display.append(" from  \"").append(record.getOriginalValue()).append("\"");
		display.append(" to  \"").append(record.getModifiedValue()).append("\"");
	    }
	    break;
	case DELETED:
	    display.append(" Delete: ").append(record.getFieldName());
	    break;

	default:
	    break;
	}

	display.append("\n");
	if (!record.getRecords().isEmpty()) {
	    // Recursively render children
	    for (int i = 0; i < record.getRecords().size(); i++) {
		DiffRecord child = record.getRecords().get(i);
		renderDiffRecord(display, child, displayNumber.concat(".").concat(String.valueOf(i + 1)));
	    }
	}

    }
}
