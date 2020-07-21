package model.creatures;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.ValueParameters;
import tools.TestFrameWork;
import model.creatures.AbilityScores.InvalidityCode;

public interface AbilityScoresTestInterface<T extends AbilityScoresArguments> 
extends TestFrameWork<AbilityScores, T>{
	/**
	 * Generates the arguments for an an {@link AbilityScores} object to run 
	 * the tests. The
	 * implementation of this method must not deep copy values before passing 
	 * it to the constructor of the object.
	 * @param description	of the object.
	 * @param values		to store in the object.
	 * @param errors		expected when validating the object.
	 * @return an implementation of the {@link AbilityScoresArguments} abstract
	 * class to initialise an {@link AbilityScores} object with the input 
	 * values.
	 */
	T createAbilityScoresArguments(String description, Map<AbilityName, Integer> values,
			Map<AbilityName, InvalidityCode> errors);
	
	/**
	 * @return a {@link Stream} containing all the values in 
	 * {@link AbilityName}.
	 */
	default Stream<AbilityName> abilityNames(){
		return Arrays.stream(AbilityName.values());
	}
	/**
	 * Appends the name of the ability to the name of the test case.
	 * @param name		of the test case.
	 * @param ability	being tested.
	 * @return a {@link String} containing the names of the test case and the
	 * ability.
	 */
	default String testName(String name, AbilityName ability) {
		return String.format("%s (%s)", name, ability);
	}
	/**
	 * Generates data to initialise {@link AbilityScoresArguments} objects
	 * to initialise valid and invalid {@link AbilityScores} objects, including
	 * an empty one.
	 * @return	a {@link Stream} containing the data described above.
	 */
	default Stream<T> argumentsSupplier() {
		return Stream.concat(validArgumentsSupplier(), invalidArgumentsSupplier(true));
	}
	/**
	 * Generates data to initialise {@link AbilityScoresArguments} objects
	 * to initialise valid and invalid {@link AbilityScores} objects.
	 * @param includeEmpty if true, the arguments to initialise an empty
	 * {@link AbilityScores} will be present in the return value. Else, it will
	 * not.
	 * @return	a {@link Stream} containing the data described above.
	 */
	default Stream<T> argumentsSupplier(boolean includeEmpty){
		return Stream.concat(validArgumentsSupplier(), invalidArgumentsSupplier(includeEmpty));
	}
	/**
	 * Generates data to initialise {@link AbilityScoresArguments} objects
	 * to initialise valid {@link AbilityScores} objects.
	 * @return	a {@link Stream} containing the data described above.
	 */
	default Stream<T> validArgumentsSupplier(){
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
		return IntStream.range(0, valueMaps.length).mapToObj(i
				-> createAbilityScoresArguments(names[i], valueMaps[i], validityMaps[i]));
	}
	/**
	 * Generates data to initialise {@link AbilityScoresArguments} objects
	 * to initialise invalid {@link AbilityScores} objects, including an empty
	 * object.
	 * @return	a {@link Stream} containing the data described above.
	 */
	default Stream<T> invalidArgumentsSupplier(){
		return invalidArgumentsSupplier(true);
	}
	/**
	 * Generates data to initialise {@link AbilityScoresArguments} objects
	 * to initialise invalid {@link AbilityScores} object.
	 * @param includeEmpty	if true, the arguments to initialise an empty
	 * {@link AbilityScores} will be present in the return value. Else, it will
	 * not.
	 * @return	a {@link Stream} containing the data described above.
	 */
	default Stream<T> invalidArgumentsSupplier(boolean includeEmpty) {
		@SuppressWarnings("unchecked")
		Map<AbilityName, Integer>[] valueMaps = new HashMap[includeEmpty ? 4 : 3];
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
		if(includeEmpty) {
			index = 3;
			for(AbilityName ability: AbilityScores.MANDATORY_ABILITIES) {
				validityMaps[index].put(ability, InvalidityCode.MISSING);
			}
			names[index] = "empty map";
		}
		//Format return value
		return IntStream.range(0, valueMaps.length).mapToObj(i
				-> createAbilityScoresArguments(names[i], valueMaps[i], validityMaps[i]));
	}
	
	/**
	 * Checks that {@link AbilityScores#getModifier(AbilityName)} returns 
	 * a modifier consistent with {@link AbilityScore#getModifier()} for the 
	 * corresponding {@link AbilityScore} object.
	 */
	@TestFactory
	default Stream<DynamicNode> getModifier() {
		return testContainer("getModifier(AbilityName)", args 
				-> {
					AbilityScores scores = args.convert();
					return abilityNames().map(ability 
							-> {
								int expected = args.values.containsKey(ability) ?
										AbilityScore.computeModifier(args.values.get(ability)) : 0;
										return new AbstractMap.SimpleEntry<>(ability.toString(), 
												() -> assertEquals(expected, scores.getModifier(ability)));
							});
				});
	}

	/**
	 * Checks that {@link AbilityScores#getScore(AbilityName)} returns 
	 * a score consistent with the input value for the 
	 * corresponding {@link AbilityScore} object.
	 */
	@TestFactory
	default Stream<DynamicNode> getScore() {
		return testContainer("getScore(abilityName)", args -> getScore_Helper(args));
	}
	
	/**
	 * Returns a {@link Stream} of {@link Executable} that checks that
	 * {@link AbilityScores#getScore(AbilityName)} returns the expected value 
	 * for all the {@link AbilityName} in the given map, and an undefined value
	 * for the others.
	 * @param args		used to generate the {@link AbilityScores} object 
	 * under test.
	 */
	public default Stream<Map.Entry<String, Executable>> getScore_Helper(T args) {
		AbilityScores scores = args.convert();
		return abilityNames().map(ability
				-> {
					boolean isDefined = args.values.containsKey(ability);
					return new AbstractMap.SimpleEntry<String, Executable>(
							String.format(" (%sability %s)", isDefined?"":"undefined ", ability),
							() -> {
								if(isDefined) {
									assertEquals(args.values.get(ability).intValue(), 
											scores.getScore(ability).getValue());
								} else {
									assertFalse(scores.getScore(ability).isDefined());
								}
							});
				});
	}
	/**
	 * Checks that the {@link AbilityScores} implementation main constructor 
	 * initialises an object that is not backed by the input map of values.
	 */
	@TestFactory
	default Stream<DynamicNode> deepCopyInputMap() {
		return testContainer(argumentsSupplier(false), "AbilityScores(Map) (deep copy input)", 
				args -> deepCopyInputMap(args));
	}

	/**
	 * Checks that the {@link AbilityScores} implementation main constructor 
	 * initialises an object that is not backed by the input map of values.
	 * @param args	to initialise the object to test.
	 */
	default Stream<Map.Entry<String, Executable>> deepCopyInputMap(T args) {
		/* Magic number to modify a bit the values. */
		int modif = 2;
		AbilityScores scores = args.convert();
		//Modify values after instantiation of the ability scores
		for(AbilityName ability: args.values.keySet()) {
			args.values.put(ability, args.values.get(ability) + modif);
		}
		return args.values.keySet().stream().map(ability
				-> new AbstractMap.SimpleEntry<>("(" + ability.toString() + ")",
						() -> assertEquals(args.values.get(ability) - modif,
								scores.getScore(ability).getValue())
						));
	}
	/**
	 * Checks that 
	 * {@link AbilityScores#checkValidity()} returns
	 * a map of {@link InvalidityCode} objects consistent with the stored
	 * {@link AbilityScore} objects.
	 */
	@TestFactory
	default Stream<DynamicTest> checkValidity() {
		return test("checkValidity()", args -> assertEquals(args.errors, args.convert().checkValidity()));
	}
	/**
	 * Checks that {@link AbilityScores#iterator()} returns all the abilities 
	 * from {@link AbilityName} in the right order and then is exhausted.
	 */
	@TestFactory
	default Stream<DynamicNode> iterator_getAllAbilityNames() {
		return argumentsSupplier().map(args
				-> {
					String message = testName("iterator() (get all ability names)", args);
					Iterator<Map.Entry<AbilityName, AbilityScore>> iter = args.convert().iterator();
					return dynamicContainer(message, Stream.of(
							iterator_getAllAbilityNames(message, iter),
							dynamicTest(message + " (reached the end)", () -> assertFalse(iter.hasNext()))));
				});
	}
	/**
	 * Checks that {@link AbilityScores#iterator()} returns all the abilities 
	 * from {@link AbilityName} in the right order.
	 */
	default DynamicTest iterator_getAllAbilityNames(String message, 
			Iterator<Map.Entry<AbilityName, AbilityScore>> iter) {
		return dynamicTest(message + " (all abilities)", () -> assertAll(
				abilityNames().map(ability -> () -> assertEquals(ability, iter.next().getKey(),
						() -> String.format("%s: missing %s", message, ability)))));
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
	@TestFactory
	default Stream<DynamicNode> iterator_getAllValues() {
		return testContainer("iterator() (get all values)", args -> {
			AbilityScores abilities = args.convert();
			Iterator<Map.Entry<AbilityName, AbilityScore>> iter = abilities.iterator();
			return abilityNames().map(ability
					-> iterator_getAllValues(abilities, ability, iter));
		});
	}
	/**
	 * Checks that {@link AbilityScores#iterator()} returns the right ability 
	 * score from the {@link AbilityScores} object backing it.
	 * @param abilities backed by the iterator
	 * @param ability	which value is tested
	 * @param iter		object under test
	 */
	default AbstractMap.SimpleEntry<String, Executable> iterator_getAllValues(AbilityScores abilities, 
			AbilityName ability, Iterator<Map.Entry<AbilityName, AbilityScore>> iter){
		return new AbstractMap.SimpleEntry<>(ability.toString(), () -> {
			if(abilities.getScore(ability).isDefined()) {
				assertEquals(abilities.getScore(ability), iter.next().getValue());
			} else {
				assertFalse(iter.next().getValue().isDefined());
			}
		});
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns true on two
	 * objects containing the same values.
	 */
	@TestFactory
	default Stream<DynamicTest> equals_true() {
		return test("equals(Object) (case true)", args 
				-> assertEquals(args.convert(), args.convert()));
	}
	/**
	 * Checks that {@link AbilityScores#hashCode()} returns the same result when 
	 * used several times on the same object.
	 */
	@TestFactory
	default Stream<DynamicTest> hashCode_consistentResult(){
		return test("hashCode() (consistent results)", args
				-> {
					AbilityScores scores = args.convert();
					assertEquals(scores.hashCode(), scores.hashCode());
				});
	}
	/**
	 * Checks that {@link AbilityScores#hashCode()} returns the same result when 
	 * used on two equal objects.
	 */
	@TestFactory
	default Stream<DynamicTest> hashCode_Equality() {
		return test("hashCode() (equality)", args
				-> assertEquals(args.convert().hashCode(), args.convert().hashCode()));
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns false on two
	 * objects containing different values.
	 */
	@TestFactory
	default Stream<DynamicTest> equals_falseIncrement() {
		return test(argumentsSupplier(false), "equals(Object) (case false: values modified)", args
				-> {
					AbilityScores scores = args.convert();
					args.values.forEach((key, value) 
							-> args.values.put(key, value 
									+ (value == ValueParameters.MAX_ABILITY_SCORE ? -1 : 1)));
					assertNotEquals(scores, args.convert());
				});
	}
	/**
	 * Checks that {@link AbilityScores#equals(Object)} returns false on two
	 * objects containing different keys.
	 */
	@TestFactory
	default Stream<DynamicNode> equals_falseMoreAbilities() {
		return testContainer(argumentsSupplier(false),
				"equals(Object) (case false: missing ability)", args
				-> {
					AbilityScores first = args.convert();
					args.values.clear();
					AbilityScores second = args.convert();
					return Stream.of(
							new AbstractMap.SimpleEntry<String, Executable>(
									" (a != b)", () -> assertNotEquals(first, second)),
							new AbstractMap.SimpleEntry<String, Executable>(
									" (b != a)", () -> assertNotEquals(second, first)));
				});
	}
}