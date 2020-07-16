package model.creatures;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.AbilityScoreTestInterface;
import model.values.ValueParameters;

public class RWAbilityScoresTest implements AbilityScoreTestInterface, AbilityScoresTestInterface, 
CommittablePartTest<RWAbilityScores, RWAbilityScores> {

	@Override
	public AbilityScore createAbilityScore(Integer value, boolean isDefined) {
		RWAbilityScores builder = new RWAbilityScores((Map<AbilityName, Integer>) null);
		AbilityName ability = AbilityName.values()[((value % AbilityName.values().length) 
				+ AbilityName.values().length) % AbilityName.values().length];
		builder.setAbilityScore(ability, value);
		if(!isDefined) {
			builder.setAbilityScore(ability, null);
		}
		return builder.getScore(ability);
	}

	@Override
	public RWAbilityScores createAbilityScores(Map<AbilityName, Integer> values) {
		return new RWAbilityScores(values);
	}
	
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} properly
	 * sets the value on a valid input and returns null.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (valid value) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setValidValue(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		int value = (ValueParameters.MAX_ABILITY_SCORE + ValueParameters.MIN_ABILITY_SCORE) / 2;
		assertAll(() -> assertNull(abilities.setAbilityScore(setAbility, value)),
				() -> assertEquals(value, abilities.getScore(setAbility).getValue()),
				() -> assertEquals(abilities.getScore(setAbility), ROabilities.getScore(setAbility)));
	}
	
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} does not
	 * modify the other values on a valid non-null input.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (valid value, check side effect) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setValidValue_noSideEffect(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		int value = (ValueParameters.MAX_ABILITY_SCORE + ValueParameters.MIN_ABILITY_SCORE) / 2;
		abilities.setAbilityScore(setAbility, value);
		checkNoSideEffect(values, abilities, ROabilities, setAbility);
	}
	
	/**
	 * Forges a Function that returns an executable asserting that the input 
	 * {@link RWAbilityScores} and {@link ROAbilityScores} contain their 
	 * expected value.
	 * @param values
	 * @param abilities
	 * @param ROabilities
	 * @return
	 */
	private static void checkNoSideEffect(Map<AbilityName, Integer> values,
			RWAbilityScores abilities, AbilityScores ROabilities, AbilityName setAbility){
		Function<AbilityName, Executable> testFactory = (ability) -> {
			return () -> {
				assertAll(
						() -> assertEquals(abilities.getScore(ability), 
								ROabilities.getScore(ability)),
						() -> {
							if(values.get(ability) == null) {
								assertFalse(abilities.getScore(ability).isDefined(),
										"Ability " + ability + " should not be defined");
							} else {
								assertEquals(values.get(ability), 
										abilities.getScore(ability).getValue());
							}
						}
						);
			};
		};
		assertAll(Arrays.asList(AbilityName.values()).stream().filter((ability) -> (ability != setAbility))
				.map(testFactory));
	}
	
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} properly
	 * sets the value and returns the corresponding 
	 * {@link AbilityScores.InvalidityCode} on an invalid value. Does not 
	 * handle missing mandatory values.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (invalid value) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setInvalidValue(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		Function<Map.Entry<InvalidityCode, Integer>, Executable> testFactory = (entry) -> {
			return () -> {
				assertAll(() -> assertEquals(entry.getKey(), 
						abilities.setAbilityScore(setAbility, entry.getValue())),
						() -> assertEquals(entry.getValue(), abilities.getScore(setAbility).getValue()),
						() -> assertEquals(abilities.getScore(setAbility), ROabilities.getScore(setAbility)));
			};
		};
		assertAll(forgeInvalidValues().entrySet().stream().map(testFactory));
	}
	
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} does not
	 * modify the other values when the value is too high or too low.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (invalid value, check side effect) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setInvalidValue_noSideEffect(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		Function<Map.Entry<InvalidityCode, Integer>, Executable> testFactory = (entry) -> {
			return () -> {
				abilities.setAbilityScore(setAbility, entry.getValue());
				checkNoSideEffect(values, abilities, ROabilities, setAbility);
			};
		};
		assertAll(forgeInvalidValues().entrySet().stream().map(testFactory));
	}
	
	/**
	 * Initialises a map containing invalid values for ability scores 
	 * associated with the corresponding {@link InvalidityCode}. Does not
	 * provide missing value.
	 * @return	said map.
	 */
	private static Map<InvalidityCode, Integer> forgeInvalidValues(){
		Map<InvalidityCode, Integer> inputValues = new EnumMap<>(InvalidityCode.class);
		inputValues.put(InvalidityCode.TOO_HIGH, ValueParameters.MAX_ABILITY_SCORE + 3);
		inputValues.put(InvalidityCode.TOO_LOW, ValueParameters.MIN_ABILITY_SCORE - 2);
		return inputValues;
	}
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} properly
	 * sets the value on a null input for an optional ability.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (valid null value) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setValidNullValue(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		assertAll(() -> assertNull(abilities.setAbilityScore(setAbility, null)),
				() -> assertFalse(abilities.getScore(setAbility).isDefined()),
				() -> assertEquals(abilities.getScore(setAbility), ROabilities.getScore(setAbility)));
	}
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} does not
	 * modify the other values on a valid null input.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (valid null value, check side effect) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setValidNullValue_noSideEffect(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.STRENGTH;
		abilities.setAbilityScore(setAbility, null);
		checkNoSideEffect(values, abilities, ROabilities, setAbility);
	}
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} properly
	 * sets the value on a null input for a mandatory ability and returns the
	 * consistent {@link InvalidityCode}.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (invalid null value) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setInvalidNullValue(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.DEXTERITY;
		assertAll(() -> assertEquals(InvalidityCode.MISSING, abilities.setAbilityScore(setAbility, null)),
				() -> assertFalse(abilities.getScore(setAbility).isDefined()),
				() -> assertEquals(abilities.getScore(setAbility), ROabilities.getScore(setAbility)));
	}
	/**
	 * Checks that 
	 * {@link RWAbilityScores#setAbilityScore(AbilityName, Integer)} does not
	 * modify the other values on an invalid null input.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Set ability score (invalid null value, check side effect) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void setAbilityScore_setInvalidNullValue_noSideEffect(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = createAbilityScores(values);
		AbilityScores ROabilities = abilities.getROAbilityScores();
		//TODO make this change for the different tests
		AbilityName setAbility = AbilityName.DEXTERITY;
		abilities.setAbilityScore(setAbility, null);
		checkNoSideEffect(values, abilities, ROabilities, setAbility);
	}
	
	/**
	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)}
	 * initialises an object that is a copy of the input one.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Deep copy constructor (copy) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void deepCopyConstructor_consistentValues(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores abilities = new RWAbilityScores(createAbilityScores(values));
		AbilityScoresTestInterface.getScore_Helper(values, abilities);
	}
	/**
	 * Modifies all the values of its first input and checks that those of its
	 * second input are not modified accordingly. Assumes, to begin with, that
	 * first and second contain equal values before the modifications.
	 * @param first		object that will be modified during the test.
	 * @param second	object that is supposed to contain the same data as the
	 * first when passed to the method.
	 */
	private static void deepCopyConstructorHelper_Independance(RWAbilityScores first, RWAbilityScores second) {
		int increment = 2;
		int defaultValue = 5;
		for(Map.Entry<AbilityName, AbilityScore> entry: first) {
			first.setAbilityScore(entry.getKey(), 
					entry.getValue().isDefined() ? entry.getValue().getValue() + increment : defaultValue);
		}
		assertAll(Arrays.stream(AbilityName.values()).map((ability) -> () ->
			assertNotEquals(first.getScore(ability), second.getScore(ability))));
	}
	
	/**
	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)}
	 * initialises an object that is a deep copy of the input one: any 
	 * modification of the new object is not reflected on the copied one.
	 * Does not verify that modifications of the copied object are not 
	 * reflected on the new one.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Deep copy constructor (independance of the new object) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void deepCopyConstructor_IndependanceNewFromOld(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores oldAbilities = createAbilityScores(values);
		RWAbilityScores newAbilities = new RWAbilityScores(oldAbilities);
		deepCopyConstructorHelper_Independance(newAbilities, oldAbilities);
	}
	/**
	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)}
	 * initialises an object that is a deep copy of the input one: any 
	 * modification of the copied object is not reflected on the new one.
	 * Does not verify that modifications of the new object are not reflected
	 * on the copied one.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Deep copy constructor (independance of the copied object) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void deepCopyConstructor_IndependanceOldFromNew(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores oldAbilities = createAbilityScores(values);
		RWAbilityScores newAbilities = new RWAbilityScores(oldAbilities);
		deepCopyConstructorHelper_Independance(oldAbilities, newAbilities);
	}
	/**
	 * Checks that {@link RWAbilityScores#RWAbilityScores(AbilityScores)} does
	 * not modify the input object.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "Deep copy constructor (copied object is not modified) on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES,
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void deepCopyConstructor_noSideEffect(String testCase,
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		RWAbilityScores oldAbilities = createAbilityScores(values);
		@SuppressWarnings("unused")
		RWAbilityScores newAbilities = new RWAbilityScores(oldAbilities);
		checkNoSideEffect(values, oldAbilities, oldAbilities.getROAbilityScores(), null);
	}

	/**
	 * Converts an array of {@link Arguments} objects each containing a text 
	 * and a {@link RWAbilityScores} object into a map where the abilities are
	 * indexed by their text description.
	 * @param input
	 * @return
	 */
	private Map<String, RWAbilityScores> objectFactory(Arguments[] input){
		final int KEY = 0;
		final int VALUE = 1;
		Map<String, RWAbilityScores> result = new HashMap<>();
		for(int i = 0; i < input.length; i++) {
			result.put((String) input[i].get()[KEY], 
					createAbilityScores((Map<AbilityName, Integer>) input[i].get()[VALUE]));
		}
		return result;
	}
	@Override
	public Map<String, RWAbilityScores> validObjectFactory() {
		return objectFactory(AbilityScoresTestInterface.validAbilityScoresParametersSupplier());
	}
	
	@Override
	public Map<String, RWAbilityScores> invalidObjectFactory(){
		return objectFactory(AbilityScoresTestInterface.invalidAbilityScoresParametersSupplier());
	}
	
	@Override
	public Collection<CommitTestInput<RWAbilityScores, RWAbilityScores>> validCommitParameterFactory(){
		Collection<CommitTestInput<RWAbilityScores, RWAbilityScores>> result = new ArrayList<>();
		String description;
		RWAbilityScores testObject;
		RWAbilityScores commitArg;
		description = "A full AbilityScores replaces a small one";
		testObject = new RWAbilityScores((Map<AbilityName, Integer>) null);
		int value = ValueParameters.MIN_ABILITY_SCORE;
		int increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE) 
				/ (AbilityName.values().length - 1);
		for(AbilityName ability : AbilityName.values()) {
			testObject.setAbilityScore(ability, value);
			value += increment;
		}
		commitArg = new RWAbilityScores((Map<AbilityName, Integer>) null);
		result.add(new CommitTestInput<>(description, testObject, commitArg));
		description = "A small AbilityScores replaces a full one";
		commitArg = new RWAbilityScores(testObject);
		testObject = new RWAbilityScores((Map<AbilityName, Integer>) null);
		value = ValueParameters.MIN_ABILITY_SCORE;
		increment = (ValueParameters.MAX_ABILITY_SCORE - ValueParameters.MIN_ABILITY_SCORE) 
				/ (AbilityScores.MANDATORY_ABILITIES.size() - 1);
		for(AbilityName ability : AbilityScores.MANDATORY_ABILITIES) {
			testObject.setAbilityScore(ability, value);
			value += increment;
		}
		result.add(new CommitTestInput<>(description, testObject, commitArg));
		return result;
	}
	
	@Override
	public void modifyTestObject(RWAbilityScores testObject) {
		for(Map.Entry<AbilityName, AbilityScore> entry : testObject) {
			if(entry.getValue().isDefined()) {
				testObject.setAbilityScore(entry.getKey(), 
						entry.getValue().getValue() 
						+ (entry.getValue().getValue() == ValueParameters.MAX_ABILITY_SCORE ? -1 : 1));
			}
		}
	}
}
