package model.values;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import model.exceptions.IllegalRollException;
import service.parameters.ValueParameters;

public class RollTest {
	static long seed = (long) 2;

	/**
	 * Checks that Roll with a negative number of dice and/or sides, or too 
	 * many sides and/or dice, throw an {@link IllegalRollException}.
	 */
	@Test
	public void testConstructor() {
		assertNotNull("1d6 should work", new Roll(1, 6));
		assertNotNull("3d20 should work", new Roll(3, 20));
		assertNotNull("7d7 should work", new Roll(7, 7));
		try {
			new Roll(ValueParameters.MIN_NUMBER_OF_DICE - 4, 6);
			fail("Less than MIN_NUMBER_OF_DICE (" + ValueParameters.MIN_NUMBER_OF_DICE
					+ ") dice is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(ValueParameters.MIN_NUMBER_OF_DICE - 1, 13);
			fail("Less than MIN_NUMBER_OF_DICE (" + ValueParameters.MIN_NUMBER_OF_DICE
					+ ") dice is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(1, ValueParameters.MIN_NUMBER_OF_SIDES-3);
			fail("Less than MIN_NUMBER_OF_SIDES (" + ValueParameters.MIN_NUMBER_OF_SIDES
					+ ") sides is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(5, ValueParameters.MIN_NUMBER_OF_SIDES-1);
			fail("Less than MIN_NUMBER_OF_SIDES (" + ValueParameters.MIN_NUMBER_OF_SIDES
					+ ") sides is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(ValueParameters.MIN_NUMBER_OF_DICE - 1, ValueParameters.MIN_NUMBER_OF_SIDES - 1);
			fail("Less than MIN_NUMBER_OF_DICE (" + ValueParameters.MIN_NUMBER_OF_DICE
					+ ") dice and less than MIN_NUMBER_OF_DICE ("
					+ ValueParameters.MIN_NUMBER_OF_SIDES + ") sides is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(ValueParameters.MAX_NUMBER_OF_DICE + 1, 6);
			fail("More than MAX_NUMBER_OF_DICE (" + ValueParameters.MAX_NUMBER_OF_DICE
					+ ") dice is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(5, ValueParameters.MAX_NUMBER_OF_SIDES + 1);
			fail("More than MAX_NUMBER_OF_SIDES (" + ValueParameters.MAX_NUMBER_OF_SIDES
					+ ") sides is not valid");
		} catch (IllegalRollException e) {}
		try {
			new Roll(ValueParameters.MAX_NUMBER_OF_DICE + 25, ValueParameters.MAX_NUMBER_OF_SIDES + 12);
			fail("More than MAX_NUMBER_OF_DICE (" + ValueParameters.MAX_NUMBER_OF_DICE
					+ ") dice and more than MAX_NUMBER_OF_DICE ("
					+ ValueParameters.MAX_NUMBER_OF_SIDES + ") sides is not valid");
		} catch (IllegalRollException e) {}
	}

	/**
	 * Tests that the {@link Roll#roll()} method returns results consistent 
	 * with {@link Roll#getResults()}.
	 */
	@Test
	public void testRollSum() {
		/*
		 * XXX This method only tests with one type of dice.
		 * When Roll handle several types of dice at the same time, 
		 * this method will have to be adapted.
		 * XXX This method does not take bonuses into account. When Roll handle
		 * them, this method will have to be adapted.
		 */
		Roll monoDie = new Roll(1, 12); //Tests with 1 die
		Roll multiDice = new Roll(10, 20); //Tests with 10 dice
		Roll.seed(seed);
		for(int i = 0; i < 1000; i++) {
			int actualMono = monoDie.roll();
			int actualMulti = multiDice.roll();
			assertEquals("The result of throwing one die is the value of the die",
					monoDie.getResults().get(12)[0], actualMono);
			int expectedMulti = 0;
			for (int j:multiDice.getResults().get(20)) {
				expectedMulti += j;
			}
			assertEquals("The result of throwing multiple dice is the sum of their values", 
					expectedMulti, actualMulti);
		}
	}
	
	/**
	 * Tests that the {@link Roll#getResults()} method returns results 
	 * consistent with the dice.
	 */
	@Test
	public void testGetResultsDiceConsistency() {
		/*
		 * XXX this method only tests with one type of dice.
		 * When Roll handle several types of dice at the same time, 
		 * this method will have to be adapted
		 */
		int firstNumberOfDice = 1;
		int firstNumberOfSides = 20;
		int secondNumberOfDice = 3;
		int secondNumberOfSides = 6;
		Roll.seed(seed);
		Roll firstRoll = new Roll(firstNumberOfDice, firstNumberOfSides);
		Roll secondRoll = new Roll(secondNumberOfDice, secondNumberOfSides);
		for (int i = 0; i < 1000; i++) {
			int[] firstResults = firstRoll.getResults().get(firstNumberOfSides);
			int[] secondResults = secondRoll.getResults().get(secondNumberOfSides);
			assertEquals("Roll.getResults() does not have the expected size", 
					firstNumberOfDice, firstResults.length);
			assertEquals("Roll.getResults() does not have the expected size", 
					secondNumberOfDice, secondResults.length);
			for (int j = 0; j < firstResults.length; j++) {
				assertTrue("Roll.getResults() must produces integers between 1 and number of"
						+ "sides, obtained " + firstResults[j], 
						firstResults[j] > 0 && firstResults[j] <= firstNumberOfSides);
			}
			for (int j = 0; j < secondResults.length; j++) {
				assertTrue("Roll.getResults() must produces integers between 1 and number of"
						+ "sides, obtained " + secondResults[j], 
						secondResults[j] > 0 && secondResults[j] <= secondNumberOfSides);
			}
		}
	}
	
	/**
	 * Ensures that seeding the Roll is a static operation and 
	 * that equally sided dice using the same seed roll the same number.
	 */
	@Test
	public void testSeed() {
		int  numberOfSides = 6;
		Roll first = new Roll(1, numberOfSides);
		Roll second = new Roll(1,numberOfSides);
		Roll pair = new Roll(2,numberOfSides);
		Roll.seed(seed);
		int[] resultSeparate = new int[] {first.roll(), second.roll()};
		Roll.seed(seed);
		int[] resultPair = pair.getResults().get(numberOfSides);
		for (int i = 0; i < resultPair.length; i++) {
			assertEquals("Seeded dice should roll the same results", resultPair[i], resultSeparate[i]);
		}
	}
	

	/**
	 * Ensures that getResults rolls the dice if need be and that
	 * it always returns the same results if called successively without 
	 * rolling the dice.
	 */
	@Test 
	public void testGetResults() {
		/*
		 * XXX this method only tests with one type of dice.
		 * When Roll handle several types of dice at the same time, 
		 * this method will have to be adapted
		 */
		//First, test with only one dice.
		int numberOfSides = 53;
		Roll first = new Roll(1,numberOfSides);
		Roll second = new Roll(1,numberOfSides);
		Roll.seed(seed);
		//checks that getResults rolls the dice if necessary 
		Map<Integer, int[]> firstResults = first.getResults();
		Roll.seed(seed);
		int secondRoll = second.roll();
		Map<Integer, int[]> secondResults = second.getResults();
		Map<Integer, int[]> firstResults2 = first.getResults();
		Map<Integer, int[]> secondResults2 = second.getResults();
		/*
		 * With the same seed, firstResults and secondResults should be equal.
		 * Without rolling the dice, firstResults2 and secondResults2 should 
		 * be equal to firstResults and secondResults.
		 */
		checkResultsEquality(1, firstResults, secondResults, firstResults2, secondResults2);
		//Roll again, and make sure the roll is not equal to the first
		int rollAgain;
		do {
			rollAgain = second.roll();
		}
		while(rollAgain == secondRoll);
		Map<Integer, int[]> getResultsAgain = second.getResults();
		checkGetResultsSize(getResultsAgain, 1);
		try {
			assertArrayEquals(getResultsAgain.get(numberOfSides), secondResults.get(numberOfSides));
			fail("Two getResults() separated by a roll() can only be equal if the rolls are equal");
		} catch (AssertionError e) {}
		//Second, test again with several dice
		numberOfSides = 79;
		int numberOfDice = 10;
		Roll multipleDice = new Roll(numberOfDice, numberOfSides);
		//Check that getResults rolls the dice if need be, and only then.
		Map<Integer, int[]> multipleResults = multipleDice.getResults();
		Map<Integer, int[]> multipleResults2 = multipleDice.getResults();
		checkResultsEquality(1, multipleResults, multipleResults2);
	}
	
	/**
	 * Checks that the number of keys in the map is equal to the number of 
	 * types of dice rolled.
	 * @param map			result of a roll.
	 * @param numberOfTypes
	 */
	private void checkGetResultsSize(Map<Integer, int[]> map, int numberOfTypes) {
		assertEquals("Roll.getResults() must return a map consistent with the number of types of die",
				numberOfTypes, map.size());
	}
	/**
	 * Verifies that all the provided maps are equal. Fails with an 
	 * {@link AssertionError} otherwise.
	 * @param numberOfTypes	number of types of dice thrown.
	 * @param maps			results of calling {@link Roll#getResults()}.
	 */
	@SafeVarargs
	final private void checkResultsEquality(int numberOfTypes, Map<Integer, int[]>... maps) {
		for (int i = 0; i < maps.length; i++){
			checkGetResultsSize(maps[i], numberOfTypes);
			for(int k : maps[0].keySet()) {
					assertArrayEquals("Two getResults without rolling the dice should return the same results",
							maps[0].get(k), maps[i].get(k));
			}
		}
		
	}

}
