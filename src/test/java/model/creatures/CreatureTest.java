package model.creatures;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

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
		assertEquals("A creature starts in the initial phase",
				Creature.InitStatus.ABILITIES, creature.getInitialisationStatus());
		AbilityScores scores = AbilityScores.create(AbilityScoresTest.basicAbilityScores());
		creature.setAbilityScores(scores);
		//Check that the get is consistent with the set
		AbilityScores results = creature.getAbilityScores();
		for(AbilityName value: AbilityName.values()) {
			assertEquals("getAbilityScores must return the model.values set by setAbilityScores", 
					scores.getScore(value).getValue(), results.getScore(value).getValue());
		}
		assertEquals("setAbilityScores must move the initialisation status forward",
				Creature.InitStatus.REVIEW, creature.getInitialisationStatus());
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
	
	@Test
	public void testIsInitialised() {
		Creature creature = new Creature();
		assertFalse("A non initialised creature is not complete", creature.isInitialised());
		creature.setAbilityScores(AbilityScores.create(AbilityScoresTest.basicAbilityScores()));
		assertFalse("A creature with only ability scores is not initialised", creature.isInitialised());
		creature.finish();
		assertTrue("A fully initialised creature is initialised", creature.isInitialised());
	}
	
	/**
	 * Checks that {@link Creature#EDITION_STATUSES} consists of all statuses 
	 * except for {@link Creature.InitStatus#COMPLETED} and in the right order.
	 */
	@Test
	public void testGetEditionStatuses() {
		Set<Creature.InitStatus> set = Creature.EDITION_STATUSES;
		//Check that EDITION_STATUSES contains every status except COMPLETED
		for(Creature.InitStatus status : Creature.InitStatus.values()) {
			if(status != Creature.InitStatus.COMPLETED) {
				assertTrue("EDITION_STATUSES must contain all intermediate statuses", set.contains(status));
			} else {
				assertFalse("EDITION_STATUSES must no contain COMPLETED", set.contains(status));
			}
		}
		//Check that all the statuses in EDITION_STATUSES are in the right order
		int i = 0;
		Creature.InitStatus[] statuses = Creature.InitStatus.values();
		for(Creature.InitStatus status : Creature.EDITION_STATUSES) {
			assertEquals("EDITION_STATUSES is supposed to be a sorted set", statuses[i], status);
			i++;
		}
	}

}