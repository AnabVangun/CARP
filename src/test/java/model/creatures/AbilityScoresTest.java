package model.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.ValueParameters;
import model.creatures.AbilityScores.InvalidityCode;

public class AbilityScoresTest {
	/*
	 * XXX when mutability is added to RWAbilityScores, remember to check that
	 * the associated ROAbilityScores changes as well.
	 */

	/**
	 * Checks that the {@link AbilityScores}
	 * constructor accepts a map with valid parameters (abilities with 
	 * valid values: all, some or none) and rejects a
	 * map with invalid values (some 
	 * abilities with invalid values) and null.
	 * Additional test: changing the input map to the constructor does not
	 * affect the object.
	 * Also checks the {@link 
	 * AbilityScores#getModifier(model.creatures.AbilityScores.AbilityName)} method to 
	 * verify the constructor.
	 */
	@Test
	public void testConstructor() {
		testConstructorHelper(abilities -> (new RWAbilityScores(abilities)));
		testConstructorHelper(abilities -> AbilityScores.create(abilities));
	}
	/**
	 * Checks that the input function 
	 * accepts a map with valid parameters (abilities with 
	 * valid values: all, some or none) and rejects a
	 * map with invalid values (some 
	 * abilities with invalid values) and null.
	 * Additional test: changing the input map to the constructor does not
	 * affect the object.
	 * Also checks the {@link 
	 * AbilityScores#getModifier(model.creatures.AbilityScores.AbilityName)} method to 
	 * verify the constructor.
	 */
	private void testConstructorHelper(Function<Map<AbilityName, Integer>, AbilityScores> function) {
		AbilityScores test;
		//OK case: all abilities with valid values
		EnumMap<AbilityName, Integer> abilities = basicAbilityScores();
		assertNotNull("All abilities with valid values makes a valid AbilityScores object",
				test = function.apply(abilities));
		//Verify the values
		for(AbilityName ability: abilities.keySet()) {
			assertEquals("The constructor must not modify the valid values",
					abilities.get(ability).intValue(), test.getScore(ability).getValue());
		}
		//OK case: all mandatory abilities with valid values
		abilities.clear();
		int i = ValueParameters.MIN_ABILITY_SCORE;
		for(AbilityName ability : AbilityScores.MANDATORY_ABILITIES) {
			abilities.put(ability, i);
			i++;
		}
		assertNotNull("All mandatory abilities with valid values makes a valid AbilityScores object",
				test = function.apply(abilities));
		for(AbilityName ability : AbilityName.values()) {
			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
				assertEquals("The constructor must not modify the valid values", 
						abilities.get(ability).intValue(), test.getScore(ability).getValue());
			} else {
				assertNull("getScore must return null for the unspecified values",
						test.getScore(ability));
			}
		}
		//changing the input map after instantiation does not affect the object
		for(AbilityName ability : abilities.keySet()) {
			abilities.put(ability, abilities.get(ability)+1);
		}
		for(AbilityName ability : AbilityName.values()) {
			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
				assertEquals("The constructor should make a deep copy of the input map", 
						abilities.get(ability) - 1, test.getScore(ability).getValue());
			} else {
				assertNull("getScore must return null for the unspecified model.values",
						test.getScore(ability));
			}
		}
		/*
		 * KO case: some abilities with invalid values
		 * Invalid values do not raise exceptions anymore
		 */
//		for (i = 0; i < AbilityName.values().length ; i ++) {
//			abilities.clear();
//			for (int j = i; j >= 0; j--) {
//				abilities.put(AbilityName.values()[j], ValueParameters.MAX_ABILITY_SCORE + 1);
//				try {
//					function.apply(abilities);
//					fail("The constructor should fail on invalid values");
//				} catch (IllegalArgumentException e) {}
//			}
//		}
		//KO case: null input
		try {
			function.apply((Map<CreatureParameters.AbilityName,Integer>) null);
		} catch (NullPointerException e) {};
	}

	/**
	 * Checks that 
	 * {@link AbilityScores#getModifier(model.creatures.AbilityScores.AbilityName)}
	 * is consistent {@link AbilityScore#computeModifier(int)} for given values
	 * and that it returns 0 for non-initialised model.values.
	 * This test covers both {@link RWAbilityScores} and {@link ROAbilityScores}.
	 */
	@Test
	public void testGetModifier() {
		//test each value between min and max ability score
		int i = ValueParameters.MIN_ABILITY_SCORE;
		EnumMap<AbilityName, Integer> abilities = 
				new EnumMap<AbilityName, Integer>(AbilityName.class);
		AbilityName[] names = AbilityName.values();
		while(i < ValueParameters.MAX_ABILITY_SCORE) {
			for(AbilityName ability : names) {
				abilities.put(ability, i);
				if(i < ValueParameters.MAX_ABILITY_SCORE) {
					i++;
				}
			}
			AbilityScores RWtest = new RWAbilityScores(abilities);
			AbilityScores ROtest = ((RWAbilityScores) RWtest).getROAbilityScores();
			for(AbilityName ability : names) {
				assertEquals("The two methods must be equal",
						AbilityScore.computeModifier(abilities.get(ability)),
						RWtest.getModifier(ability));
				assertEquals("The read-only ability scores must return consistent values",
						RWtest.getModifier(ability),
						ROtest.getModifier(ability));
			}
		}
		//test that non-initialised model.values return 0
		abilities.clear();
		for(AbilityName ability : AbilityScores.MANDATORY_ABILITIES) {
			abilities.put(ability, 10);
		}
		AbilityScores RWtest = new RWAbilityScores(abilities);
		AbilityScores ROtest = ((RWAbilityScores) RWtest).getROAbilityScores();
		for(AbilityName ability : names) {
			assertEquals("Abilities non initialised or with score 10 have a modifier of 0",
					0, RWtest.getModifier(ability));
			assertEquals("The read-only ability scores must return consistent model.values",
					RWtest.getModifier(ability),
					ROtest.getModifier(ability));
		}
	}

	/**
	 * Checks that {@link AbilityScores#iterator()} returns an iterator 
	 * consistent with the content of the {@link AbilityScores}, and this for
	 * both of its subclasses.
	 */
	@Test
	public void testIterator() {
		//Generate an input for the AbilityScores constructor
		EnumMap<AbilityName, Integer> abilities = basicAbilityScores();
		//Generate a RW and a RO AbilityScores
		AbilityScores rwTest = new RWAbilityScores(abilities);
		AbilityScores roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
		//Verify the iterator
		testIteratorHelper(rwTest);
		testIteratorHelper(roTest);
		//Remove all non mandatory abilities and check again
		for (AbilityName name : AbilityName.values()) {
			if(!AbilityScores.MANDATORY_ABILITIES.contains(name)) {
				abilities.remove(name);
			}
		}
		rwTest = new RWAbilityScores(abilities);
		roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
		testIteratorHelper(rwTest);
		testIteratorHelper(roTest);
	}

	/**
	 * Checks that the iterator returned by 
	 * {@link AbilityScores#iterator()} iterates over all defined values in 
	 * the {@link AbilityScores} used to build it. Also checks that the values
	 * are the same as in the object.
	 * @param test	an initialised {@link AbilityScores} object.
	 * @throws AssertionError	if one of the sub-tests fails.
	 */
	private void testIteratorHelper(AbilityScores test) {
		//Set used to check that all keys in expected are found in test
		EnumSet<AbilityName> names = EnumSet.allOf(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry : test) {
			assertTrue("The iterator should not return a name twice",
					names.remove(entry.getKey()));
			assertEquals("The value in the iterator must be equal to the value in the object",
					test.getScore(entry.getKey()), entry.getValue());
		}
		//Check that all model.values remaining in names are not found in test
		for(AbilityName name : names) {
			assertNull("A name not returned by the iterator must be a missing value", 
					test.getScore(name));
		}
	}

	/**
	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)} 
	 * returns a {@link RWAbilityScores} object that is a deep copy of the 
	 * input {@link AbilityScores} object.
	 */
	@Test
	public void testDeepCopyConstructor() {
		//Generate an input for the AbilityScores constructor
		EnumMap<AbilityName, Integer> abilities = basicAbilityScores();
		//Generate a RW and a RO AbilityScores
		AbilityScores rwTest = new RWAbilityScores(abilities);
		AbilityScores roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
		testDeepCopyConstructorHelper(rwTest);
		testDeepCopyConstructorHelper(roTest);
		//Go again after removing non-mandatory abilities
		for (AbilityName name : AbilityName.values()) {
			if(!AbilityScores.MANDATORY_ABILITIES.contains(name)) {
				abilities.remove(name);
			}
		}
		rwTest = new RWAbilityScores(abilities);
		roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
		testDeepCopyConstructorHelper(rwTest);
		testDeepCopyConstructorHelper(roTest);
	}

	/**
	 * Checks that the copy constructor indeed makes a deep copy of the input
	 * {@link AbilityScores} object.
	 * @param input	an initialised {@link AbilityScores} object.
	 * @throws AssertionError	if one of the sub-tests fails.
	 */
	private void testDeepCopyConstructorHelper(AbilityScores input) {
		AbilityScores test = new RWAbilityScores(input);
		//Define how to check in one way
		Consumer<AbilityScores> oneWayChecker = new Consumer<AbilityScores>() {
			@Override
			public void accept(AbilityScores t) {
				//iterate over the entries of one object
				for(Map.Entry<AbilityName, AbilityScore> entry : t) {
					//If a value is null in one of the AbilityScores, it must be so in the other
					assertTrue("An AbilityScore must be null in none or both the AbilityScores",
							(input.getScore(entry.getKey()) == null && test.getScore(entry.getKey()) == null)
							||(input.getScore(entry.getKey()) != null && test.getScore(entry.getKey()) != null));
					if(input.getScore(entry.getKey()) != null) {
						assertNotSame("The copy must be a deep copy", 
								input.getScore(entry.getKey()), test.getScore(entry.getKey()));
					}
				}
			}
		};
		//Check both ways to make sure no AbilityScore is missed.
		oneWayChecker.accept(input);
		oneWayChecker.accept(test);
	}

	/**
	 * @return a map with a different value for each ability score, starting 
	 * at 10.
	 */
	public static EnumMap<AbilityName, Integer> basicAbilityScores() {
		EnumMap<AbilityName, Integer> abilities = 
				new EnumMap<AbilityName, Integer>(AbilityName.class);
		int i = - AbilityName.values().length / 2;
		for(AbilityName ability : AbilityName.values()) {
			abilities.put(ability, 10+i);
			i++;
		}
		return abilities;
	}
	
	/**
	 * Checks that {@link AbilityScores#checkValidity()} and 
	 * {@link AbilityScores#checkAbilityScoresValidity(Map)} return a map
	 * containing an entry for each invalid entry.
	 */
	@Test
	public void testCheckValidity() {
		testCheckValidityHelper(abilities -> (new RWAbilityScores(abilities)));
		testCheckValidityHelper(abilities -> AbilityScores.create(abilities));
	}
	
	private void testCheckValidityHelper(Function<Map<AbilityName, Integer>, AbilityScores> function) {
		final Map<AbilityName, InvalidityCode> expected = new EnumMap<AbilityName, InvalidityCode>(AbilityName.class);
		/*
		 * Helper method.
		 * Remove some mandatory abilities from its first argument, and add 
		 * them to its second argument as missing
		 */
		BiConsumer<Map<AbilityName, Integer>, Map<AbilityName, InvalidityCode>> removeAbility = 
				(abilities, result) -> {
					int i = 0;
					for(AbilityName toRemove: AbilityScores.MANDATORY_ABILITIES) {
						if (i%2 == 0) {
							if(abilities.remove(toRemove) == null) {
								fail("The ability should have been removed, something went wrong");
							}
							result.put(toRemove, InvalidityCode.MISSING);
						}
						i++;
					}
				};
		/*
		 * Helper method.
		 * Modify the value associated with some keys in its first argument
		 * to make them illegally low, and add 
		 * them to its second argument as too low.
		 */
		BiConsumer<Map<AbilityName, Integer>, Map<AbilityName, InvalidityCode>> lowerAbility =
				(abilities, result) -> {
					int i = 0;
					for(AbilityName toModify: abilities.keySet()) {
						if(i%2 == 0) {
							if(result.get(toModify) != null) {
								fail("The ability should not already be an error case");
							}
							abilities.put(toModify, ValueParameters.MIN_ABILITY_SCORE - (i+1));
							result.put(toModify, InvalidityCode.TOO_LOW);
						}
					}
				};
		/*
		 * Helper method.
		 * Modify the value associated with some keys in its first argument
		 * to make them illegally high, and add 
		 * them to its second argument as too high.
		 */
		BiConsumer<Map<AbilityName, Integer>, Map<AbilityName, InvalidityCode>> raiseAbility =
				(abilities, result) -> {
					int i = 0;
					for(AbilityName toModify: abilities.keySet()) {
						if(i%2 == 1) {
							if(result.get(toModify) != null) {
								fail("The ability should not already be an error case");
							}
							abilities.put(toModify, ValueParameters.MAX_ABILITY_SCORE + (i+1));
							result.put(toModify, InvalidityCode.TOO_HIGH);
						}
					}
				};
		/*
		 * Helper method.
		 * Initialise all test variables, set up the input test conditions and 
		 * verify that the result is consistent with the expectations.
		 */
		Consumer<BiConsumer<Map<AbilityName, Integer>, Map<AbilityName, InvalidityCode>>> performTest = 
				(setUpTest) -> {
					expected.clear();
					AbilityScores test;
					Map<AbilityName, Integer> input;
					input = basicAbilityScores();
					setUpTest.accept(input, expected);
					test = function.apply(input);
					assertEquals(expected, test.checkValidity());
					assertEquals(expected, AbilityScores.checkAbilityScoresValidity(input));
				};
		//Case 1: some mandatory abilities missing
		performTest.accept((abilities, result) -> removeAbility.accept(abilities, result));
		//Case 2: some abilities with values too low
		performTest.accept((abilities, result) -> lowerAbility.accept(abilities, result));
		//Case 3: some abilities with values too high
		performTest.accept((abilities, result) -> raiseAbility.accept(abilities, result));
		//Case 4: some mandatory abilities missing and some abilities too low
		performTest.accept((abilities, result) -> {
			removeAbility.accept(abilities, result);
			lowerAbility.accept(abilities, result);
		});
		//Case 5: some mandatory abilities missing and some abilities too high
		performTest.accept((abilities, result) -> {
			removeAbility.accept(abilities, result);
			raiseAbility.accept(abilities, result);
		});
		//Case 6: mandatory abilities missing, abilities too low and too high
		performTest.accept((abilities, result) -> {
			removeAbility.accept(abilities, result);
			lowerAbility.accept(abilities, result);
			raiseAbility.accept(abilities, result);
		});
		//Case 7: nothing wrong
		performTest.accept((abilities, result) -> {});
	}
}
