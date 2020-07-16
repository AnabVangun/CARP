package model.creatures;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
				/ (AbilityName.values().length - 1);
		for(AbilityName ability : AbilityName.values()) {
			valueMaps[index].put(ability, value);
			value += increment;
		}
		names[index] = "map full of valid values";
		//Second: map with only mandatory values
		index = 1;
		value = ValueParameters.MIN_ABILITY_SCORE;
		increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE)
				/ (AbilityScores.MANDATORY_ABILITIES.size() - 1);
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
	
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns true on two
	 * objects containing the same values.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "abilityScores equals: case true on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void equals_true(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		assertAll(
				()->assertEquals(createAbilityScores(values), createAbilityScores(values)),
				()->assertEquals(createAbilityScores(values).hashCode(), createAbilityScores(values).hashCode()));
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns false on two
	 * objects containing different values.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "abilityScores equals: case false (values modified) on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void equals_falseIncrement(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		assumeFalse(values.isEmpty(), "Empty ability scores cannot be modified just a bit");
		AbilityScores first = createAbilityScores(values);
		values.forEach((key, value) 
				-> values.put(key, value 
							+ (value == ValueParameters.MAX_ABILITY_SCORE ? -1 : 1)));
		assertNotEquals(first, createAbilityScores(values));
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns false on two
	 * objects containing different values.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "abilityScores equals: case false (missing ability) on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void equals_falseMoreAbilities(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		assumeFalse(values.isEmpty(), "Cannot remove abilities from an empty map");
		AbilityScores first = createAbilityScores(values);
		for(AbilityName ability : values.keySet()) {
			values.remove(ability);
			break;
		}
		AbilityScores second = createAbilityScores(values);
		assertAll(
				() -> assertNotEquals(first, second),
				() -> assertNotEquals(second, first));
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns false when the
	 * argument is an {@link AbilityScore} object contained in it.
	 * @param testCase	description of the test case
	 * @param values	map of the values used to initialise the 
	 * {@link AbilityScores} object.
	 * @param error		map of the {@link InvalidityCode} corresponding to the
	 * values.
	 */
	@ParameterizedTest(name = "abilityScores equals: case false (comparison with ability score) on {0}")
	@MethodSource({VALID_ABILITY_SCORES, INVALID_ABILITY_SCORES})
	default void equals_abilityScore(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		AbilityScores abilities = createAbilityScores(values);
		assertAll(Arrays.stream(AbilityName.values()).map(ability
				-> () -> assertNotEquals(abilities, abilities.getScore(ability))));
	}
}