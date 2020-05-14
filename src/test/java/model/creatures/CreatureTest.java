package model.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;

import org.junit.Test;

import model.creatures.AbilityScores;
import model.creatures.Creature;
import model.creatures.RWAbilityScores;
import model.exceptions.IllegalAbilityScoreException;
import service.parameters.CreatureParameters.AbilityName;

public class CreatureTest {

	/**
	 * Checks the {@link Creature#setAbilityScores(AbilityScores)} and 
	 * {@link Creature#getAbilityScores()} methods.
	 */
	@Test
	public void testSetAbilityScores() {
		Creature creature = new Creature();
		//Generate ability scores
		AbilityScores scores;
		EnumMap<AbilityName, Integer> abilities = 
				new EnumMap<AbilityName, Integer>(AbilityName.class);
		int i = 0;
		for(AbilityName ability : AbilityName.values()) {
			abilities.put(ability, 10+i);
			i++;
		}
		scores = new RWAbilityScores(abilities);
		creature.setAbilityScores(scores);
		//Check that the get is consistent with the set
		AbilityScores results = creature.getAbilityScores();
		for(AbilityName value: AbilityName.values()) {
			assertEquals("getAbilityScores must return the model.values set by setAbilityScores", 
					scores.getScore(value).getValue(), results.getScore(value).getValue());
		}
		//Check that setAbilityScores rejects null input
		try {
			creature.setAbilityScores(null);
			fail("A creature should throw an exception when trying to set null ability scores");
		} catch (IllegalAbilityScoreException e) {}
		/*
		 * XXX when AbilityScores implements mutability, check that the 
		 * creature makes a deep copy of the object.
		 */
	}

}