/**
 * 
 */
package com.perago.techtest.impl;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.perago.techtest.Diff;
import com.perago.techtest.DiffEngine;
import com.perago.techtest.DiffException;
import com.perago.techtest.DiffRenderer;
import com.perago.techtest.test.Person;

/**
 * @author Jabu
 *
 */
public class DiffRendererImplTest {
    private DiffRenderer diffRenderer;
    private DiffEngine diffEngine;

    @Before
    public void setUp() throws Exception {
	diffRenderer = new DiffRendererImpl();
	diffEngine = new DiffEngineService();
    }

    @Test
    public void testRenderExample1() throws DiffException {
	Person original = null;
	Person modified = createFredSmithPerson();

	Diff<Person> diff = diffEngine.calculate(original, modified);
	StringBuilder expected = new StringBuilder("1 Create: Person\n");
	expected.append("1.1 Create: firstName as \"Fred\"\n");
	expected.append("1.2 Create: surname as \"Smith\"").append("\n");
	expected.append("1.3 Create: friend as \"null\"").append("\n");
	expected.append("1.4 Create: nickNames as \"null\"").append("\n");

	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
    }

    @Test
    public void testRenderExample2() throws DiffException {
	Person original = createFredSmithPerson();
	Person modified = null;

	Diff<Person> diff = diffEngine.calculate(original, modified);
	StringBuilder expected = new StringBuilder("1 Delete: Person\n");

	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
    }

    @Test
    public void testRenderExample4() throws DiffException {
	Person original = createFredSmithPerson();

	Person friend = createPerson("Tom", "Brown");
	Person modified = createFredSmithPerson();
	modified.setFriend(friend);
	Diff<Person> diff = diffEngine.calculate(original, modified);

	StringBuilder expected = new StringBuilder("1 Update: Person\n");
	expected.append("1.1 Update: friend").append("\n");
	expected.append("1.1.1 Create: Person").append("\n");
	expected.append("1.1.1.1 Create: firstName as \"Tom\"").append("\n");
	expected.append("1.1.1.2 Create: surname as \"Brown\"").append("\n");
	expected.append("1.1.1.3 Create: friend as \"null\"").append("\n");
	expected.append("1.1.1.4 Create: nickNames as \"null\"").append("\n");

	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
    }

    @Test
    public void testRenderExample5() throws DiffException {
	Person original = createFredSmithPerson();
	Person tomBrown = createPerson("Tom", "Brown");
	original.setFriend(tomBrown);
	Person jimBrown = createPerson("Jim", "Brown");
	Person modified = createFredJonesPerson();
	modified.setFriend(jimBrown);

	Diff<Person> diff = diffEngine.calculate(original, modified);

	StringBuilder expected = new StringBuilder("1 Update: Person\n");
	expected.append("1.1 Update: surname from  \"Smith\" to  \"Jones\"").append("\n");
	expected.append("1.2 Update: friend").append("\n");
	expected.append("1.2.1 Update: Person").append("\n");
	expected.append("1.2.1.1 Update: firstName from  \"Tom\" to  \"Jim\"").append("\n");

	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
    }

    @Test
    public void testRenderExample7() throws DiffException {
	Person original = createFredSmithPerson();
	Person tomBrown = createPerson("Tom", "Brown");
	original.setFriend(tomBrown);
	Person modified = createPerson("John", "Smith");
	Diff<Person> diff = diffEngine.calculate(original, modified);

	StringBuilder expected = new StringBuilder("1 Update: Person\n");
	expected.append("1.1 Update: firstName from  \"Fred\" to  \"John\"").append("\n");
	expected.append("1.2 Update: friend").append("\n");
	expected.append("1.2.1 Delete: Person").append("\n");

	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
    }

    @Test
    public void testRenderExample8() throws DiffException {
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

	StringBuilder expected = new StringBuilder("1 Update: Person\n");
	expected.append("1.1 Update: nickNames from  \"[biff, scooter]\" to  \"[biff, polly]\"").append("\n");
	String actual = diffRenderer.render(diff);
	assertEquals(expected.toString().trim(), actual.trim());
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

}
