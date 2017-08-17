package com.perago.techtest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.perago.techtest.Diff;
import com.perago.techtest.DiffEngine;
import com.perago.techtest.DiffException;
import com.perago.techtest.impl.DiffAction;
import com.perago.techtest.impl.DiffEngineService;
import com.perago.techtest.impl.DiffRecord;
import com.perago.techtest.test.Person;

public class DiffEngineServiceTest {
    private DiffEngine diffEngine;

    @Before
    public void setUp() throws Exception {
	diffEngine = new DiffEngineService();
    }

    @Test
    public void testCalculateWithCreateAction() throws DiffException {
	Person original = null;
	Person modified = createFredSmithPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	assertCalculate(DiffAction.CREATED, diff.getRecord().getAction(),
		"A null original object diffed against non-null modified object must reflect as being “created” in diff.",
		original, modified, diff.getRecord().getOriginalValue(), diff.getRecord().getModifiedValue());
	assertFalse("There should be diff records as original is not the same as modified.",
		diff.getRecord().getRecords().isEmpty());

	for (DiffRecord record : diff.getRecord().getRecords()) {
	    switch (record.getFieldName()) {
	    case "firstName":
		assertEquals("The new value for FirstName should be \"Fred\"", "Fred", record.getModifiedValue());
		break;
	    case "surname":
		assertEquals("The new value for Surname should be \"Smith\"", "Smith", record.getModifiedValue());
		break;

	    default:
		assertEquals("The other field should have a null value", null, record.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "A null original object diffed against non-null modified object must reflect as being “created” in diff.",
		    DiffAction.CREATED, record.getAction());

	}
    }

    @Test
    public void testCalculateWithDeleteAction() throws DiffException {
	Person modified = null;
	Person original = createFredSmithPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	assertCalculate(DiffAction.DELETED, diff.getRecord().getAction(),
		"A non-null original object diffed against null modified object must reflect as being “deleted” in diff",
		original, modified, diff.getRecord().getOriginalValue(), diff.getRecord().getModifiedValue());
	assertTrue("There should be no diff records as the whole object was deleted",
		diff.getRecord().getRecords().isEmpty());

    }

    @Test
    public void testCalculateWithUpdateAction() throws DiffException {
	Person original = createFredSmithPerson();

	Person friend = createPerson("Tom", "Brown");
	Person modified = createFredJonesPerson();
	modified.setFriend(friend);

	Diff<Person> diff = diffEngine.calculate(original, modified);
	assertCalculate(DiffAction.UPDATED, diff.getRecord().getAction(),
		"Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		original, modified, diff.getRecord().getOriginalValue(), diff.getRecord().getModifiedValue());
	assertTrue("Only surname and friend should be updated as they have been modified",
		diff.getRecord().getRecords().size() == 2);

	for (DiffRecord record : diff.getRecord().getRecords()) {
	    switch (record.getFieldName()) {
	    case "surname":
		assertEquals("The new value for Surname should be \"Jones\"", "Jones", record.getModifiedValue());
		break;
	    case "friend":
		assertCreateFreindOnUpdate(friend, record.getRecords().get(0));
		break;

	    default:
		assertEquals("The other field should have a null value", null, record.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		    DiffAction.UPDATED, record.getAction());

	}
    }

    @Test
    public void testApplyForCreateAction() throws DiffException {
	Person original = null;
	Person modified = createFredSmithPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	Person actual = diffEngine.apply(original, diff);
	assertEquals("The result returned from diffService.apply(original,diff) must be equal to modified", modified,
		actual);
    }

    @Test
    public void testApplyForDeleteAction() throws DiffException {
	Person modified = null;
	Person original = createFredSmithPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	Person actual = diffEngine.apply(original, diff);
	assertEquals("The result returned from diffService.apply(original,diff) must be equal to modified", modified,
		actual);
    }

    @Test
    public void testApplyForUpdateAction() throws DiffException {
	Person original = createFredSmithPerson();

	Person modified = createFredJonesPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	Person actual = diffEngine.apply(original, diff);
	assertEquals("The result returned from diffService.apply(original,diff) must be equal to modified", modified,
		actual);
    }

    @Test
    public void testCalculateExample5() throws DiffException {
	Person original = createFredSmithPerson();
	Person tomBrown = createPerson("Tom", "Brown");
	original.setFriend(tomBrown);
	Person jimBrown = createPerson("Jim", "Brown");
	Person modified = createFredJonesPerson();
	modified.setFriend(jimBrown);
	Diff<Person> diff = diffEngine.calculate(original, modified);

	assertTrue("Only firstName and friend should be updated as they have been modified",
		diff.getRecord().getRecords().size() == 2);

	for (DiffRecord record : diff.getRecord().getRecords()) {
	    switch (record.getFieldName()) {
	    case "surname":
		assertEquals("The new value for Surname should be \"Jones\"", "Jones", record.getModifiedValue());
		break;
	    case "friend":

		assertEquals(
			"Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
			DiffAction.UPDATED, record.getAction());
		assertEquals(
			"Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
			DiffAction.UPDATED, record.getRecords().get(0).getAction());
		assertEquals("The Person object fro freind should be updated", "Person",
			record.getRecords().get(0).getFieldName());
		for (DiffRecord diffRecord : record.getRecords().get(0).getRecords()) {
		    switch (diffRecord.getFieldName()) {
		    case "firstName":
			assertEquals("The new value for FirstName should be \"Jim\"", "Jim",
				diffRecord.getModifiedValue());
			break;
		    case "surname":
			assertEquals("The new value for Surname should be \"Brown\"", "Brown",
				diffRecord.getModifiedValue());
			break;

		    default:
			assertEquals("The other field should have a null value", null, diffRecord.getModifiedValue());
			break;
		    }
		    assertEquals(
			    "Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
			    DiffAction.UPDATED, diffRecord.getAction());
		}

		break;

	    default:
		assertEquals("The other field should have a null value", null, record.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		    DiffAction.UPDATED, record.getAction());

	}
    }

    @Test
    public void testCalculateExample7() throws DiffException {
	Person original = createFredSmithPerson();
	Person tomBrown = createPerson("Tom", "Brown");
	original.setFriend(tomBrown);
	Person modified = createPerson("John", "Smith");
	Diff<Person> diff = diffEngine.calculate(original, modified);

	for (DiffRecord record : diff.getRecord().getRecords()) {
	    switch (record.getFieldName()) {
	    case "firstName":
		assertEquals("The new value for firstName should be \"John\"", "John", record.getModifiedValue());
		break;
	    case "friend":
		assertEquals(
			"Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
			DiffAction.UPDATED, record.getAction());
		assertEquals("The friend should be deleted", DiffAction.DELETED,
			record.getRecords().get(0).getAction());

		break;

	    default:
		assertEquals("The other field should have a null value", null, record.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		    DiffAction.UPDATED, record.getAction());

	}
    }

    @Test
    public void testCalculateExample8() throws DiffException {
	Person original = createFredSmithPerson();
	Set<String> origNickNames = new TreeSet<>();
	origNickNames.add("scooter");
	origNickNames.add("biff");
	original.setNickNames(origNickNames);

	Person modified = createFredSmithPerson();
	Set<String> modiNickNames = new TreeSet<>();
	modiNickNames.add("biff");
	modiNickNames.add("polly");
	modified.setNickNames(modiNickNames);

	Diff<Person> diff = diffEngine.calculate(original, modified);

	assertCalculate(DiffAction.UPDATED, diff.getRecord().getAction(),
		"Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		original, modified, diff.getRecord().getOriginalValue(), diff.getRecord().getModifiedValue());

	for (DiffRecord record : diff.getRecord().getRecords()) {
	    switch (record.getFieldName()) {
	    case "nickNames":
		assertEquals("The new value for nickNames should be \"[biff, polly]\"", "[biff, polly]",
			record.getModifiedValue());
		break;

	    default:
		assertEquals("The other field should have a null value", null, record.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "Non-null original object diffed against non-null modified object must reflect as being “updated” if there are properties of original whose values differ from the properties of modified",
		    DiffAction.UPDATED, record.getAction());

	}

    }

    private Person createPerson(String firstName, String surname) {
	Person friend = new Person();
	friend.setFirstName(firstName);
	friend.setSurname(surname);
	return friend;
    }

    private Person createFredJonesPerson() {
	return createPerson("Fred", "Jones");
    }

    private Person createFredSmithPerson() {
	return createPerson("Fred", "Smith");
    }

    private void assertCreateFreindOnUpdate(Person friend, DiffRecord record) {
	assertEquals(
		"A null original object diffed against non-null modified object must reflect as being “created” in diff.",
		DiffAction.CREATED, record.getAction());
	assertTrue("4 New Friend fields should be created", record.getRecords().size() == 4);
	for (DiffRecord diffRecord : record.getRecords()) {
	    switch (diffRecord.getFieldName()) {
	    case "firstName":
		assertEquals("The new value for FirstName should be \"Tom\"", "Tom", diffRecord.getModifiedValue());
		break;
	    case "surname":
		assertEquals("The new value for Surname should be \"Brown\"", "Brown", diffRecord.getModifiedValue());
		break;

	    default:
		assertEquals("The other field should have a null value", null, diffRecord.getModifiedValue());
		break;
	    }
	    assertEquals(
		    "A null original object diffed against non-null modified object must reflect as being “created” in diff.",
		    DiffAction.CREATED, diffRecord.getAction());
	}
    }

    private void assertCalculate(DiffAction expectedAction, DiffAction actualAction, String actionMessage,
	    Object expectedOriginalValue, Object expectedNewValue, Object actualOriginalValue, Object ectualNewValue) {
	assertEquals(actionMessage, expectedAction, actualAction);
	assertEquals("The orginal value should be as it was before modification", expectedOriginalValue,
		actualOriginalValue);
	assertEquals("The new value should be the one set  after modifications", expectedNewValue, ectualNewValue);
    }

}
