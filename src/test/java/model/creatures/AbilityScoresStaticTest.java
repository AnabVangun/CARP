package model.creatures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;

public class AbilityScoresStaticTest implements AbilityScoresTestInterface{

	/**
	 * Checks that {@link AbilityScores#create(Map)} returns a properly built
	 * {@link AbilityScores} object by running all the tests from 
	 * {@link AbilityScoresTestInterface} on it.
	 */
	@Override
	public AbilityScores createAbilityScores(Map<AbilityName, Integer> values) {
		return AbilityScores.create(values);
	}
	/**
	 * Checks that 
	 * {@link AbilityScores#checkAbilityScoresValidity(java.util.Map)} throws 
	 * a NullPointerException on a null input.
	 */
	@Test
	void checkAbilityScoresValidity_nullInput() {
		assertThrows(NullPointerException.class, () -> AbilityScores.checkAbilityScoresValidity(null));
	}
	
	/**
	 * Checks that 
	 * {@link AbilityScores#checkAbilityScoresValidity(java.util.Map)} returns
	 * a map of {@link InvalidityCode} objects consistent with the input.
	 * @param testCase
	 * @param values
	 * @param error
	 */
	@ParameterizedTest(name = "checkAbilityScoresValidity on {0}")
	@MethodSource({AbilityScoresTestInterface.VALID_ABILITY_SCORES, 
		AbilityScoresTestInterface.INVALID_ABILITY_SCORES})
	void checkAbilityScoresValidity_notNullInput(String testCase, 
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> error) {
		assertEquals(error, AbilityScores.checkAbilityScoresValidity(values));
	}
}
