package model.creatures;

import java.util.Map;

import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;
import tools.TestArguments;
import tools.TestFrameWork;

public class ROAbilityScoresTest implements AbilityScoresTestInterface<ROAbilityScoresArguments>, 
TestFrameWork<AbilityScores, ROAbilityScoresArguments> {

	@Override
	public String testName(String methodName, ROAbilityScoresArguments args) {
		return testName(ROAbilityScores.class.getSimpleName(), methodName, args);
	}

	@Override
	public ROAbilityScoresArguments createAbilityScoresArguments(String description, Map<AbilityName, Integer> values,
			Map<AbilityName, InvalidityCode> errors) {
		return new ROAbilityScoresArguments(description, values, errors);
	}

}

class ROAbilityScoresArguments extends AbilityScoresArguments implements TestArguments<AbilityScores>{
	ROAbilityScoresArguments(String description, Map<AbilityName, Integer> values,
			Map<AbilityName, InvalidityCode> errors) {
		super(description, values, errors);
	}

	@Override
	public ROAbilityScores convert() {
		return (ROAbilityScores) new RWAbilityScoresArguments(super.description, super.values, super.errors)
				.convert().getROAbilityScores();
	}
}