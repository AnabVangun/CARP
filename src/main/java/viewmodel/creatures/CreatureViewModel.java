package viewmodel.creatures;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import model.creatures.AbilityScores;
import model.creatures.Creature;
import model.values.AbilityScore;
import service.parameters.CreatureParameters.AbilityName;;

/**
 * View model used to display creatures.
 * Mock implementation to test the MVVM model.
 * @author TLM
 *
 */
public class CreatureViewModel {
	private final Map<AbilityName, String> scores = new HashMap<>();
	private final Map<AbilityName, String> modifiers = new HashMap<>();

	public CreatureViewModel() {
		// XXX create mock creature to try out
		Creature creature = new Creature();
		EnumMap<AbilityName, Integer> abilities = new EnumMap<AbilityName, Integer>(AbilityName.class);
		abilities.put(AbilityName.STRENGTH, 10);
		abilities.put(AbilityName.DEXTERITY, 11);
		abilities.put(AbilityName.CONSTITUTION, 12);
		abilities.put(AbilityName.INTELLIGENCE, 13);
		abilities.put(AbilityName.WISDOM, 14);
		abilities.put(AbilityName.CHARISMA, 15);
		creature.setAbilityScores(AbilityScores.create(abilities));
		formatCreature(creature);
	}
	
	private void formatCreature(Creature creature) {
		for(AbilityName name: AbilityName.values()) {
			AbilityScore ability = creature.getAbilityScores().getScore(name);
			scores.put(name, String.format("%,d", ability.getValue()));
			modifiers.put(name, String.format("%+,d", ability.getModifier()));
		}
	}
	
	public Map<AbilityName, String> getReadOnlyScores() {
		return Collections.unmodifiableMap(scores);
	}
	
	public Map<AbilityName, String> getReadOnlyModifiers(){
		return Collections.unmodifiableMap(modifiers);
	}

}
