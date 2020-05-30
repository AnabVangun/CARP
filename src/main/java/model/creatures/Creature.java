/**
 * 
 */
package model.creatures;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import service.exceptions.NotYetImplementedException;

/**
 * Actor in the game. Can be a player character, a non player character, or a 
 * monster.
 * @author TLM
 */
public class Creature {
	/**
	 * State of the initialisation of the creature. The steps must be followed
	 * in the proper order.
	 * @author TLM
	 */
	public static enum InitStatus{
		/** The next step in the process is to generate ability scores.*/
		ABILITIES,
		/** The creature is ready for a final review.*/
		REVIEW,
		/** The initialisation process has been completed.*/
		COMPLETED
	}
	/**
	 * Sorted set of all the {@link InitStatus} values that a creature go 
	 * through during its initialisation process.
	 */
	public static final Set<InitStatus> EDITION_STATUSES = 
			Collections.unmodifiableSet(EnumSet.of(InitStatus.ABILITIES, InitStatus.REVIEW));
	private RWAbilityScores abilities;
	private InitStatus initStatus = InitStatus.ABILITIES;

	/**
	 * Initialise an empty creature.
	 * After this constructor has been called, the creature is not fully
	 * initialised yet: it needs to go through every step of the initialisation
	 * process.
	 */
	public Creature() {}
	
	/**
	 * Sets the creature's {@link AbilityScores} and propagates the modifiers
	 * wherever applicable.
	 * @param abilities valid set of abilities. A deep copy is made.
	 */
	public void setAbilityScores(AbilityScores abilities) {
		this.abilities = new RWAbilityScores(abilities);
		this.incrementStatus();
	}
	
	/**
	 * @return a read-only view of the creature's {@link AbilityScores}.
	 */
	public AbilityScores getAbilityScores() {
		if(this.abilities == null) {
			return null;
		}
		return this.abilities.getROAbilityScores();
	}
	
	/**
	 * @return the phase of the creation process in which the creature is, or
	 * {@link InitStatus#COMPLETED} if the creature is fully initialised.
	 */
	public InitStatus getInitialisationStatus() {
		return this.initStatus;
	}
	
	/**
	 * This method is only here temporarily to set up the GUI.
	 */
	@Deprecated
	public void finish() {
		this.initStatus = InitStatus.COMPLETED;
	}
	
	/**
	 * @return true if and only if the creature is fully initialised.
	 */
	public boolean isInitialised() {
		return this.initStatus == InitStatus.COMPLETED;
	}

	/**
	 * Advances the initialisation process by one step.
	 */
	private void incrementStatus() {
		switch(this.initStatus) {
		case ABILITIES:
			this.initStatus = InitStatus.REVIEW;
			return;
		default:
			throw new NotYetImplementedException();
		}
	}
}
