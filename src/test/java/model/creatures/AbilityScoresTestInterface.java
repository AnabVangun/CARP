package model.creatures;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.ValueParameters;
import model.creatures.AbilityScores.InvalidityCode;

public interface AbilityScoresTestInterface {
	static final String VALID_ABILITY_SCORES = "model.creatures.AbilityScoresTestInterface#"
			+ "validAbilityScoresParametersSupplier";
	static final String INVALID_ABILITY_SCORES = "model.creatures.AbilityScoresTestInterface#"
			+ "invalidAbilityScoresParametersSupplier";
	/**
	 * Generates an {@link AbilityScores} object to run the tests. The
	 * implementation of this method must not deep copy values before passing 
	 * it to the constructor of the object.
	 * @param values	to store in the object.
	 * @return an {@link AbilityScores} object initialised with the input 
	 * values.
	 */
	AbilityScores createAbilityScores(Map<AbilityName, Integer> values);
	
	/**
	 * Generates datasets to initialise valid {@link AbilityScores} object:
	 * arguments containing a String describing the data, a map between 
	 * {@link AbilityName} and
	 * Integer objects, and a map between {@link AbilityScores} and 
	 * {@link InvalidityCode} that remains empty.
	 * @return	the array described above.
	 */
	static Arguments[] validAbilityScoresParametersSupplier() {
		@SuppressWarnings("unchecked")
		Map<AbilityName, Integer>[] valueMaps = new HashMap[2];
		@SuppressWarnings("unchecked")
		Map<AbilityName, InvalidityCode>[] validityMaps = new HashMap[valueMaps.length];
		String[] names = new String[valueMaps.length];
		for (int i = 0; i < valueMaps.length; i++) {
			valueMaps[i] = new HashMap<>();
			validityMaps[i] = new HashMap<>();
		}
		//First: map full of valid values
		int index = 0;
		int value = ValueParameters.MIN_ABILITY_SCORE;
		int increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE) 
				/ AbilityName.values().length;
		for(AbilityName ability : AbilityName.values()) {
			valueMaps[index].put(ability, value);
			value += increment;
		}
		names[index] = "map full of valid values";
		//Second: map with only mandatory values
		index = 1;
		value = ValueParameters.MIN_ABILITY_SCORE;
		increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE)
				/ AbilityScores.MANDATORY_ABILITIES.size();
		for(AbilityName ability : AbilityScores.MANDATORY_ABILITIES) {
			valueMaps[index].put(ability, value);
			value += increment;
		}
		names[index] = "map with only mandatory values";
		//Format return value
		Arguments[] result = new Arguments[valueMaps.length];
		for(int i = 0; i < result.length; i++) {
			result[i] = Arguments.of(names[i], valueMaps[i], validityMaps[i]);
		}
		return result;
	}
	/**
	 * Generates datasets to initialise invalid {@link AbilityScores} object:
	 * arguments containing a String describing the data, a map between 
	 * {@link AbilityName} and
	 * Integer objects, and a map between {@link AbilityScores} and 
	 * {@link InvalidityCode} that describes for each invalid value the reason
	 * why it is so.
	 * @return	the array described above.
	 */
	static Arguments[] invalidAbilityScoresParametersSupplier() {
		@SuppressWarnings("unchecked")
		Map<AbilityName, Integer>[] valueMaps = new HashMap[4];
		@SuppressWarnings("unchecked")
		Map<AbilityName, InvalidityCode>[] validityMaps = new HashMap[valueMaps.length];
		String[] names = new String[valueMaps.length];
		for (int i = 0; i < valueMaps.length; i++) {
			valueMaps[i] = new HashMap<>();
			validityMaps[i] = new HashMap<>();
		}
		//First: map full of values, some too high and some too low
		int index = 0;
		int size = AbilityName.values().length;
		//This needs AbilityName to have at least 5 values.
		int increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE)
				/ (2 * size / 3 - (size / 3 + 1));
		//The first size/3 parameters will be below MIN_ABILITY_SCORE
		int value = ValueParameters.MIN_ABILITY_SCORE - (size / 3 + 1) * increment;
		for(AbilityName ability : AbilityName.values()) {
			valueMaps[index].put(ability, value);
			if(value < ValueParameters.MIN_ABILITY_SCORE) {
				validityMaps[index].put(ability, InvalidityCode.TOO_LOW);
			} else if (value > ValueParameters.MAX_ABILITY_SCORE) {
				validityMaps[index].put(ability, InvalidityCode.TOO_HIGH);
			}
			value += increment;
		}
		names[index] = "map full of values, some too high and some too low";
		//Second: map with only valid values but missing mandatory values
		index = 1;
		value = ValueParameters.MIN_ABILITY_SCORE;
		boolean missing = true;
		increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE) 
				/ (AbilityName.values().length);
		for(AbilityName ability : AbilityName.values()) {
			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
				if(missing) {
					validityMaps[index].put(ability, InvalidityCode.MISSING);
				} else {
					valueMaps[index].put(ability, value);
				}
				missing = !missing;
			} else {
				valueMaps[index].put(ability, value);
			}
			value += increment;
		}
		names[index] = "map with only valid values but missing mandatory values";
		//Third: map with a mix of missing and invalid values
		index = 2;
		value = ValueParameters.MIN_ABILITY_SCORE - 1;
		for(AbilityName ability : AbilityName.values()) {
			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
				validityMaps[index].put(ability, InvalidityCode.MISSING);
			} else {
				valueMaps[index].put(ability, value);
				validityMaps[index].put(ability, InvalidityCode.TOO_LOW);
			}
		}
		names[index] = "map with a mix of missing and invalid values";
		//Fourth: empty map
		index = 3;
		for(AbilityName ability: AbilityScores.MANDATORY_ABILITIES) {
			validityMaps[index].put(ability, InvalidityCode.MISSING);
		}
		names[index] = "empty map";
		//Format return value
		Arguments[] result = new Arguments[valueMaps.length];
		for(int i = 0; i < result.length; i++) {
			result[i] = Arguments.of(names[i], valueMaps[i], validityMaps[i]);
		}
		return result;
	}
	
	/**
	 * Checks that {@link AbilityScores#getModifier(AbilityName)} returns 
	 * a modifier consistent with {@link AbilityScore#getModifier()} for the 
	 * corresponding {@link AbilityScore} object.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "Get modifier on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void getModifier(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		AbilityScores scores = createAbilityScores(values);
		Function<AbilityName, Executable> testerFactory = (ability) -> {
			return () -> {
				int expected = values.containsKey(ability) ? 
						AbilityScore.computeModifier(values.get(ability)) : 0;
				assertEquals(expected, scores.getModifier(ability),
						() -> "getModifier failed on ability " + ability);
			};
		};
		assertAll(Arrays.stream(AbilityName.values()).map(
				(ability) -> testerFactory.apply(ability)));
	}

	/**
	 * Checks that {@link AbilityScores#getScore(AbilityName)} returns 
	 * a score consistent with the input value for the 
	 * corresponding {@link AbilityScore} object.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "Get score on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void getScore(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		AbilityScores scores = createAbilityScores(values);
		getScore_Helper(values, scores);
	}
	
	/**
	 * Checks that
	 * {@link AbilityScores#getScore(AbilityName)} returns the expected value 
	 * for all the {@link AbilityName} in the given map, and an undefined value
	 * for the others.
	 * @param values
	 * @param scores
	 */
	public static void getScore_Helper(Map<AbilityName, Integer> values, 
			AbilityScores scores) {
		assertAll(Arrays.stream(AbilityName.values()).map((ability) -> () -> {
			if(values.containsKey(ability)) {
				assertEquals(values.get(ability).intValue(), 
						scores.getScore(ability).getValue(),
						() -> "getScore failed on ability " + ability);
			} else {
				assertFalse(scores.getScore(ability).isDefined(),
						() -> "getScore failed on undefined ability " + ability);
			}
		}));
	}
	
	/**
	 * Checks that the {@link AbilityScores} implementation main constructor 
	 * initialises an object that is not backed by the input map of values.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Deep copy input map on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void DeepCopyInputMap(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		AbilityScores abilities = createAbilityScores(values);
		//Modify values after instantiation of the ability scores
		for(AbilityName ability: values.keySet()) {
			values.put(ability, values.get(ability) + 2);
		}
		assertAll(values.keySet().stream().map(
				(ability) -> () -> assertEquals(values.get(ability) - 2, abilities.getScore(ability).getValue())));
	}
	
	/**
	 * Checks that 
	 * {@link AbilityScores#checkValidity()} returns
	 * a map of {@link InvalidityCode} objects consistent with the stored
	 * {@link AbilityScore} objects.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "checkValidity on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void checkValidity(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		assertEquals(error, createAbilityScores(values).checkValidity());
	}
	
	/**
	 * Checks that {@link AbilityScores#iterator()} returns all the abilities 
	 * from {@link AbilityName} in the right order.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "iterator: get all ability names on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void iterator_getAllAbilityNames(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		Iterator<Map.Entry<AbilityName, AbilityScore>> iter = createAbilityScores(values).iterator();
		Function<AbilityName, Executable> testerFactory = (ability) -> {
			return () -> {
				assertEquals(ability, iter.next().getKey());
			};
		};
		assertAll(
				() -> assertAll(Arrays.stream(AbilityName.values()).map(
				(ability) -> testerFactory.apply(ability))),
				() -> assertFalse(iter.hasNext(), 
						"The iterator must be depleted after all the ability scores"));
	}
	

	/**
	 * Checks that {@link AbilityScores#iterator()} returns all the ability 
	 * scores from the {@link AbilityScores} object backing it.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "iterator: get all values on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void iterator_getAllValues(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		AbilityScores abilities = createAbilityScores(values);
		Iterator<Map.Entry<AbilityName, AbilityScore>> iter = abilities.iterator();
		Function<AbilityName, Executable> testerFactory = (ability) -> {
			return () -> {
				if(abilities.getScore(ability).isDefined()) {
					assertEquals(abilities.getScore(ability), iter.next().getValue());
				} else {
					assertFalse(iter.next().getValue().isDefined());
				}
			};
		};
		assertAll(Arrays.stream(AbilityName.values()).map(
				(ability) -> testerFactory.apply(ability)));
	}
}

//FIXME delete this when finished
//	/**
//	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)} 
//	 * returns a {@link RWAbilityScores} object that is a deep copy of the 
//	 * input {@link AbilityScores} object.
//	 */
//	@Test
//	public void testDeepCopyConstructor() {
//		//Generate an input for the AbilityScores constructor
//		EnumMap<AbilityName, Integer> abilities = basicAbilityScores();
//		//Generate a RW and a RO AbilityScores
//		AbilityScores rwTest = new RWAbilityScores(abilities);
//		AbilityScores roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
//		testDeepCopyConstructorHelper(rwTest);
//		testDeepCopyConstructorHelper(roTest);
//		//Go again after removing non-mandatory abilities
//		for (AbilityName name : AbilityName.values()) {
//			if(!AbilityScores.MANDATORY_ABILITIES.contains(name)) {
//				abilities.remove(name);
//			}
//		}
//		rwTest = new RWAbilityScores(abilities);
//		roTest = ((RWAbilityScores) rwTest).getROAbilityScores();
//		testDeepCopyConstructorHelper(rwTest);
//		testDeepCopyConstructorHelper(roTest);
//	}
//
//	/**
//	 * Checks that the copy constructor indeed makes a deep copy of the input
//	 * {@link AbilityScores} object.
//	 * @param input	an initialised {@link AbilityScores} object.
//	 * @throws AssertionError	if one of the sub-tests fails.
//	 */
//	private void testDeepCopyConstructorHelper(AbilityScores input) {
//		AbilityScores test = new RWAbilityScores(input);
//		//Define how to check in one way
//		Consumer<AbilityScores> oneWayChecker = new Consumer<AbilityScores>() {
//			@Override
//			public void accept(AbilityScores t) {
//				//iterate over the entries of one object
//				for(Map.Entry<AbilityName, AbilityScore> entry : t) {
//					//If a value is null in one of the AbilityScores, it must be so in the other
//					assertTrue("An AbilityScore must be null in none or both the AbilityScores",
//							(input.getScore(entry.getKey()) == null && test.getScore(entry.getKey()) == null)
//							||(input.getScore(entry.getKey()) != null && test.getScore(entry.getKey()) != null));
//					if(input.getScore(entry.getKey()) != null) {
//						assertNotSame("The copy must be a deep copy", 
//								input.getScore(entry.getKey()), test.getScore(entry.getKey()));
//					}
//				}
//			}
//		};
//		//Check both ways to make sure no AbilityScore is missed.
//		oneWayChecker.accept(input);
//		oneWayChecker.accept(test);
//	}
//	
//	/**
//	 * Checks that:
//	 * 1. commit before prepareCommit fails
//	 * 2. prepareCommit fails if invalid values are present
//	 * 3. prepareCommit if the object is valid
//	 * 4. commit fails if a modification was made after the last prepareCommit
//	 * 5. commit succeeds if prepareCommit has succeeded
//	 */
//	@Test
//	public void testCommit() {
//		RWAbilityScores committedScores = new RWAbilityScores(basicAbilityScores());
//		RWAbilityScores replacedScores = new RWAbilityScores((Map<AbilityName, Integer>) null);
//		if(!committedScores.checkValidity().isEmpty()) {
//			fail("This test assumes that basicAbilityScores initialises a valid set of scores");
//		}
//		try {
//			committedScores.commit(replacedScores);
//			fail("A commit before a prepareCommit should fail");
//		} catch (IllegalStateException e) {}
//		//Check for different types of invalidity that all fail
//		try {
//			replacedScores.prepareCommit();
//			fail("An invalid abilityScores should fail to prepare");
//		} catch (IllegalStateException e) {};
//		//Add missing abilities, set invalid values
//		int i = 0;
//		for(AbilityName ability : AbilityName.values()) {
//			int value;
//			if(AbilityScores.MANDATORY_ABILITIES.contains(ability)) {
//				value = 10;
//			} else {
//				value = (i % 2 == 0 
//						? ValueParameters.MIN_ABILITY_SCORE - i - 1 
//						: ValueParameters.MAX_ABILITY_SCORE + i);
//				i++;
//			}
//			replacedScores.setAbilityScore(ability, value);
//		}
//		try {
//			replacedScores.prepareCommit();
//			fail("An invalid abilityScores should fail to prepare");
//		} catch (IllegalStateException e) {};
//		//Verify that if a modification occurs between prepare and commit, commit fails
//		committedScores.prepareCommit();
//		committedScores.setAbilityScore(AbilityName.STRENGTH, 2);
//		try {
//			committedScores.commit(replacedScores);
//			fail("A commit before a prepareCommit should fail");
//		} catch (IllegalStateException e) {}
//		//Check that prepare commit succeeds on valid scores
//		committedScores.prepareCommit();
//		committedScores.commit(replacedScores);
//		//Verify that commit has succeeded
//		for(AbilityName ability: AbilityName.values()) {
//			assertEquals("After a commit, the replaced object must be equal to the committed one",
//					committedScores.getScore(ability).isDefined(), 
//					replacedScores.getScore(ability).isDefined());
//			if(committedScores.getScore(ability).isDefined()) {
//				assertEquals("After a commit, the replaced object must be equal to the committed one",
//						committedScores.getScore(ability).getValue(),
//						replacedScores.getScore(ability).getValue());
//			}
//		}
//	}
//	/**
//	 * Checks that compareTo between two ability scores contained by an 
//	 * AbilityScores object is consistent with natural ordering and that 
//	 * undefined value are worse than any other value.
//	 */
//	@Test
//	public void testCompareTo() {
//		BiConsumer<Integer, Integer> assertSameSign = (a, b) -> {
//			assertTrue(a + " and " + b + " should have the same sign",
//					(a == 0 && b == 0) ||
//					(a < 0 && b < 0) ||
//					(a > 0 && b > 0)
//					);
//		};
//		AbilityScores abilities = AbilityScores.create(AbilityScoresTestInterface.basicAbilityScores());
//		for (AbilityName ability1 : AbilityName.values()) {
//			for (AbilityName ability2 : AbilityName.values()) {
//				//Compare ability is consistent with compare value of ability
//				assertSameSign.accept(abilities.getScore(ability1)
//						.compareTo(abilities.getScore(ability2)),
//						((Integer) abilities.getScore(ability1).getValue())
//						.compareTo(abilities.getScore(ability2).getValue()));
//				//Compare ability is asymetric
//				assertSameSign.accept(abilities.getScore(ability1)
//						.compareTo(abilities.getScore(ability2)),
//						-abilities.getScore(ability2)
//						.compareTo(abilities.getScore(ability1)));
//			}
//			AbilityScores nullAbility = AbilityScores.create(null);
//			int comparison = abilities.getScore(ability1)
//					.compareTo(nullAbility.getScore(ability1));
//			assertTrue(comparison + " must be greater than zero", comparison > 0);
//			comparison = nullAbility.getScore(ability1)
//					.compareTo(abilities.getScore(ability1));
//			assertTrue(comparison + " must be less than zero", comparison < 0);
//		}
//	}
//}
