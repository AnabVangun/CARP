package model.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;

import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.CreatureParameters.AbilityName;
import model.values.ValueParameters;

public class CreatureTest {
	private Creature creature;
	
	@Before
	public void setUp() throws Exception {
		creature = new Creature();
	}
	
	/**
	 * Checks that:
	 * 1. getAbilityScores returns an empty map before abilities have been set
	 * 2. setAbilityScore returns null when setting a valid value
	 * 3. setAbilityScore returns the proper invalidity code when setting an 
	 * invalid value
	 * 4. getAbilityScores returns all values set in the same map
	 * 5. Only getTemporaryAbilityScores is modified by setAbilityScore, not
	 * getAbilityScores.
	 */
	@Test
	public void testGetSetAbilities() {
		AbilityScores scores = creature.getAbilityScores();
		Map<AbilityName, Integer> expected = new EnumMap<AbilityName, Integer>(AbilityName.class);
		AbilityName name;
		//Check that all abilities are null before being initialised
		for(AbilityName ability : AbilityName.values()) {
			assertFalse("Ability " + ability + " has not been initialised, it must be undefined",
					scores.getScore(ability).isDefined());
		}
		//Check that setting a valid value in an ability score returns null
		name = AbilityName.DEXTERITY;
		expected.put(name, 10);
		assertNull("Assigning a valid value to an ability score must return null",
				creature.setAbilityScore(name, expected.get(name)));
		/*
		 * Check that setting an invalid value in an ability score return the 
		 * proper code.
		 */
		name = AbilityName.STRENGTH;
		expected.put(name, ValueParameters.MIN_ABILITY_SCORE - 10);
		InvalidityCode code = InvalidityCode.TOO_LOW;
		assertEquals("Assigning a too low value returns " + code,
				code,
				creature.setAbilityScore(name, expected.get(name)));
		name = AbilityName.CHARISMA;
		if(!AbilityScores.MANDATORY_ABILITIES.contains(name)) {
			fail("This test assumes that " + name + " is mandatory, which is false.");
		}
		expected.put(name, null);
		code = InvalidityCode.MISSING;
		assertEquals("Assigning a null value to a mandatory ability returns " + code,
				code,
				creature.setAbilityScore(name, expected.get(name)));
		name = AbilityName.CONSTITUTION;
		code = InvalidityCode.TOO_HIGH;
		expected.put(name, ValueParameters.MAX_ABILITY_SCORE + 20);
		assertEquals("Assigning a too high value returns " + code,
				code,
				creature.setAbilityScore(name, expected.get(name)));
		/*
		 * Check that getAbilityScores always returns the same object, and that
		 * it has not yet been modified. Only getTempAbilityScores
		 * contains the new values set.
		 */
		assertSame("The AbilityScores object returned by getAbilityScores must always be the same",
				scores, 
				creature.getAbilityScores());
		for(AbilityName ability : AbilityName.values()) {
			assertFalse("Ability " + ability + " has not been committed, it must be undefined",
					scores.getScore(ability).isDefined());
		}
		for(AbilityName ability : AbilityName.values()) {
			assertEquals("The abilities in the temp AbilityScores object must be updated by the set operations",
					expected.get(ability),
					creature.getTempAbilityScores().getScore(ability).isDefined() == false ? 
							null : creature.getTempAbilityScores().getScore(ability).getValue());
		}
	}
	
	/**
	 * Checks that:
	 * 1. A creature starts in the first step of the initialisation process
	 * 2. A creature in the initialisation process is editable
	 * 3. At each step of the initialisation process, validating the step moves
	 * forward the initialisation process.
	 * 4. The {@link Creature.InitStatus#REVIEW} step cannot be validated.
	 */
	@Test
	public void testBasicStateDiagram() {
		assertEquals("A creature starts in the first step of the initialisation process", 
				Creature.InitStatus.ABILITIES, 
				creature.getInitialisationStatus());
		assertTrue("A creature in the first step of the initialisation process is editable",
				creature.isEditable());
		assertFalse("A creature with ability scores not initialised does not validate the ability step",
				creature.validateModifStep());
		//Set valid ability scores
		Map<AbilityName, Integer> scores = AbilityScoresTest.basicAbilityScores();
		for(AbilityName ability : scores.keySet()) {
			creature.setAbilityScore(ability, scores.get(ability));
		}
		assertTrue("A creature with valid ability scores validates the ability step",
				creature.validateModifStep());
		assertEquals("A creature which has validated the ability step is under final review",
				Creature.InitStatus.REVIEW,
				creature.getInitialisationStatus()); //XXX update me when new init steps are defined
		try {
			creature.validateModifStep();
			fail("The " + Creature.InitStatus.REVIEW + " step cannot be validated.");
		} catch (IllegalStateException e) {}
	}
	
	/**
	 * Checks that:
	 * 1. A valid creature can be committed
	 * 2. A committed creature is not editable and complete
	 * 3. Editing a non-editable creature is not possible
	 * 4. A committed creature can be made editable again
	 * 5. A creature that has been made editable again can be edited 
	 * and committed
	 */
	@Test
	public void testCommitEdit() {
		//Check that a valid creature can be committed
		Map<AbilityName, Integer> scores = AbilityScoresTest.basicAbilityScores();
		for(AbilityName ability : scores.keySet()) {
			creature.setAbilityScore(ability, scores.get(ability));
		}
		creature.commit(); //This must not throw an exception
		assertFalse("A committed creature is not editable",
				creature.isEditable());
		assertEquals("A committed creature is complete",
				Creature.InitStatus.COMPLETED,
				creature.getInitialisationStatus());
		//Check that it is forbidden to edit a committed creature
		Consumer<Consumer<Creature>> illegalModificationChecker = (f) -> {
			try {
				f.accept(creature);
				fail("It is illegal to modify a committed creature");
			} catch (IllegalStateException e) {};
		};
		illegalModificationChecker.accept(t -> t.setAbilityScore(AbilityName.CHARISMA, 2));
		//Check that a committed creature can be made editable again
		creature.edit(); //This must not throw an exception
		assertTrue("A creature made editable is editable", 
				creature.isEditable());
		assertEquals("A creature made editable is by default under review",
				Creature.InitStatus.REVIEW,
				creature.getInitialisationStatus());
		creature.setAbilityScore(AbilityName.CHARISMA, 10); //This must not throw an exception
		creature.commit(); //This must not throw an exception
	}
	
	/**
	 * Checks that commit saves all the temporary modifications to the 
	 * creature:
	 * 1. ability scores
	 */
	@Test
	public void testOKCommit() {
		creature = basicCreature();
		creature.edit();
		//Modify an ability
		AbilityName ability = AbilityName.CONSTITUTION;
		int abilityValue = creature.getAbilityScores().getScore(ability).getValue() + 2;
		creature.setAbilityScore(ability, abilityValue);
		
		//Commit and check that all modifications are saved
		creature.commit();
		assertEquals("Commit must save the temporary values",
				abilityValue, 
				creature.getAbilityScores().getScore(ability).getValue());
	}
	
	/**
	 * Checks that commit throws an exception when called in an illegal state:
	 * 1. Some mandatory abilities missing
	 * 2. Some abilities with invalid values
	 */
	@Test
	public void testIllegalCommit() {
		BiConsumer<String, Consumer<Creature>> illegalStateChecker = (s, f) -> {
			try {
				f.accept(creature);
				fail(s);
			} catch (IllegalStateException e) {};
		};
		illegalStateChecker.accept("A creature with missing abilities cannot be committed",
				t -> t.commit());
		//Check with some invalid values
		creature = basicCreature();
		creature.edit();
		creature.setAbilityScore(AbilityName.CONSTITUTION, ValueParameters.MIN_ABILITY_SCORE - 3);
		illegalStateChecker.accept("A creature with invalid values cannot be committed", 
				t -> t.commit());
	}
	
	/**
	 * @return a Creature fully initialised with simple values for all 
	 * attributes.
	 */
	public static Creature basicCreature() {
		Creature creature = new Creature();
		Map<AbilityName, Integer> scores = AbilityScoresTest.basicAbilityScores();
		for(AbilityName ability : scores.keySet()) {
			creature.setAbilityScore(ability, scores.get(ability));
		}
		creature.commit();
		return creature;
	}
}