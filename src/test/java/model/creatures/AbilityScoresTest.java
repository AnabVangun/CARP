package model.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;

import model.exceptions.IllegalAbilityScoreException;
import model.values.AbilityScore;
import service.parameters.CreatureParameters.AbilityName;
import service.parameters.ValueParameters;

public class AbilityScoresTest {
	/*
	 * XXX when mutability is added to RWAbilityScores, remember to check that
	 * the associated ROAbilityScores changes as well.
	 */

	/**
	 * Checks that the {@link RWAbilityScores#RWAbilityScores(java.util.EnumMap)} 
	 * constructor accepts a map with valid parameters (all abilities with 
	 * valid model.values, all mandatory abilities with valid model.values) and rejects a
	 * map with invalid model.values (some mandatory abilities missing, some 
	 * abilities with invalid model.values, or both).
	 * Additional test: changing the input map to the constructor does not
	 * affect the object.
	 * Also checks the {@link 
	 * RWAbilityScores#getModifier(model.creatures.AbilityScores.AbilityName)} method to 
	 * verify the constructor.
	 */
	@Test
	public void testRWConstructor() {
		AbilityScores test;
		//OK case: all abilities with valid model.values
		EnumMap<AbilityName, Integer> abilities = basicAbilityScores();
		assertNotNull("All abilities with valid model.values makes a valid AbilityScores object",
				test = new RWAbilityScores(abilities));
		//Verify the model.values
		for(AbilityName ability: abilities.keySet()) {
			assertEquals("The constructor must not modify the valid model.values",
					abilities.get(ability).intValue(), test.getScore(ability).getValue());
		}
		//OK case: all mandatory abilities with valid model.values
		abilities.clear();
		int i = ValueParameters.MIN_ABILITY_SCORE;
		for(AbilityName ability : AbilityScores.MANDATORY_ABILITIES) {
			abilities.put(ability, 10-i);
			i++;
		}
		assertNotNull("All mandatory abilities with valid model.values makes a valid AbilityScores object",
				test = new RWAbilityScores(abilities));
		for(AbilityName ability : AbilityName.values()) {
			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
				assertEquals("The constructor must not modify the valid model.values", 
						abilities.get(ability).intValue(), test.getScore(ability).getValue());
			} else {
				assertNull("getScore must return null for the unspecified model.values",
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
		 * KO cases: 
		 * 1. some abilities with invalid model.values
		 * 2. mandatory ability missing
		 * 3. both
		 */
		for (int k = 1; i < 4; i++) {
			for (AbilityName mandatoryAbility : AbilityScores.MANDATORY_ABILITIES) {
				abilities.clear();
				for (AbilityName ability : AbilityName.values()) {
					if(k > 1 && ability.equals(mandatoryAbility)) {
						continue;
					}
					abilities.put(ability, 
							(k == 2 ? 10 : ValueParameters.MAX_ABILITY_SCORE + 1));
				}
				try {
					new RWAbilityScores(abilities);
					String errorMessage;
					switch (k) {
					case 1:
						errorMessage = "The constructor should fail on invalid model.values";
						break;
					case 2:
						errorMessage = "The constructor should fail if mandatory abilities are missing";
						break;
					case 3:
						errorMessage = "The constructor should fail if mandatory abilities are missing and with invalid model.values";
						break;
					default:
						errorMessage = "An unexpected error has caused the test to fail";
						break;
					}
					fail(errorMessage);
				} catch (IllegalAbilityScoreException e) {}
			}
		}
	}

	/**
	 * Checks that 
	 * {@link AbilityScores#getModifier(model.creatures.AbilityScores.AbilityName)}
	 * is consistent {@link AbilityScore#computeModifier(int)} for given model.values
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
				assertEquals("The read-only ability scores must return consistent model.values",
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
	 * {@link AbilityScores#iterator()} iterates over all defined model.values in 
	 * the {@link AbilityScores} used to build it. Also checks that the model.values
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
}
