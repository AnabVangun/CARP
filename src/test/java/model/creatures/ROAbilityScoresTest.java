package model.creatures;

import java.util.Map;

import model.creatures.CreatureParameters.AbilityName;

public class ROAbilityScoresTest implements AbilityScoresTestInterface {

	@Override
	public AbilityScores createAbilityScores(Map<AbilityName, Integer> values) {
		return new ROAbilityScores(new RWAbilityScoresTest().createAbilityScores(values));
	}

}
