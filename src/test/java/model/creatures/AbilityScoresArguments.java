package model.creatures;

import java.util.Map;

import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;
import tools.TestArguments;

public abstract class AbilityScoresArguments implements TestArguments<AbilityScores>{
	final Map<AbilityName, Integer> values;
	private final String description;
	final Map<AbilityName, InvalidityCode> errors;
	AbilityScoresArguments(String description, Map<AbilityName, Integer> values, 
			Map<AbilityName, InvalidityCode> errors){
		this.description = description;
		this.values = values;
		this.errors = errors;
	}
	@Override
	public String toString() {
		return this.description;
	}
}
