package model.creatures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;
import tools.TestArguments;
import tools.TestFrameWork;

public class AbilityScoresStaticTest implements AbilityScoresTestInterface<StaticAbilityScoresArguments>, 
TestFrameWork<AbilityScores, StaticAbilityScoresArguments>{
	@Override
	public String testName(String methodName, StaticAbilityScoresArguments args) {
		return testName(AbilityScores.class.getSimpleName(), methodName, args);
	}

	@Override
	public StaticAbilityScoresArguments createAbilityScoresArguments(String description,
			Map<AbilityName, Integer> values, Map<AbilityName, InvalidityCode> errors) {
		return new StaticAbilityScoresArguments(description, values, errors);
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
	 */
	@TestFactory
	Stream<DynamicTest> checkAbilityScoresValidity_notNullInput() {
		return test("checkAbilityScoresValidity(Map)", args 
				-> assertEquals(args.errors, AbilityScores.checkAbilityScoresValidity(args.values)));
	}
}

class StaticAbilityScoresArguments extends AbilityScoresArguments implements TestArguments<AbilityScores>{
	StaticAbilityScoresArguments(String description, Map<AbilityName, Integer> values, 
			Map<AbilityName, InvalidityCode> errors){
		super(description, values, errors);
	}
	/**
	 * Checks that {@link AbilityScores#create(Map)} returns a properly built
	 * {@link AbilityScores} object by running all the tests from 
	 * {@link AbilityScoresTestInterface} on it.
	 */
	@Override
	public AbilityScores convert() {
		return AbilityScores.create(values);
	}
}