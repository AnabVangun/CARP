/**
 * 
 */
package model.creatures;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
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
	private RWAbilityScores abilities = new RWAbilityScores((Map<AbilityName, Integer>) null);
	private RWAbilityScores tmpAbilities;
	private InitStatus initStatus = InitStatus.ABILITIES;

	/**
	 * Initialise an empty creature.
	 * After this constructor has been called, the creature is not fully
	 * initialised yet: it needs to go through every step of the initialisation
	 * process.
	 */
	public Creature() {
		this.prepareTmpValues();
	}
	
	/**
	 * Sets one of the creatures' temporary {@link AbilityScore}.
	 * It will become definitive after a call to {@link Creature#commit()}.
	 * @param ability	name of the ability to set.
	 * @param value		of the ability, may be null.
	 * @return	null if the value is valid, or the reason why the value is 
	 * invalid otherwise.
	 * @throws IllegalStateException if the creature is not editable.
	 */
	public AbilityScores.InvalidityCode setAbilityScore(AbilityName ability, Integer value){
		if(!this.isEditable()) {
			throw new IllegalStateException("Tried to modify an ability score of a non-editable creature");
		} else {
			return this.tmpAbilities.setAbilityScore(ability, value);
		}
	}
	
	/**
	 * @return a read-only view of the creature's {@link AbilityScores}.
	 */
	public AbilityScores getAbilityScores() {
		return this.abilities.getROAbilityScores();
	}
	
	/**
	 * @return a read-only view of the creature's current temporary 
	 * {@link AbilityScores}. These scores will become definitive after a call
	 * to {@link Creature#commit()}.
	 */
	public AbilityScores getTempAbilityScores() {
		return this.tmpAbilities.getROAbilityScores();
	}
	
	/**
	 * @return the phase of the creation process in which the creature is, or
	 * {@link InitStatus#COMPLETED} if the creature is fully initialised.
	 */
	public InitStatus getInitialisationStatus() {
		return this.initStatus;
	}
	
	/**
	 * @return true if and only if the creature can be edited.
	 */
	public boolean isEditable() {
		return this.initStatus != InitStatus.COMPLETED;
	}
	
	/**
	 * Commits all the current modifications to the creature, making them 
	 * permanent. The new state of the creature must be valid: each part of 
	 * the creature must be properly initialised, all mandatory values must 
	 * present and all present values must be valid.
	 * After a commit, the creature is not editable anymore.
	 * @throws IllegalStateException if the modifications are not valid.
	 */
	public void commit() {
		this.tmpAbilities.prepareCommit();
		/*
		 * TODO add the relevant validity tests when new parts of the creature
		 * object are implemented.
		 */
		this.tmpAbilities.commit(this.abilities);
		this.tmpAbilities = null;
		this.initStatus = InitStatus.COMPLETED;
	}
	
	/**
	 * Make the creature editable. A call to 
	 * {@link Creature#commit()}  must be made for the modifications to be 
	 * effectively ported to the creature.
	 */
	public void edit() {
		prepareTmpValues();
		this.initStatus = InitStatus.REVIEW;
	}
	
	/**
	 * Initialise all temporary values to the same values as the permanent 
	 * ones.
	 */
	private void prepareTmpValues() {
		tmpAbilities = new RWAbilityScores(this.abilities);
	}

	/**
	 * Validates the current step of the initialisation process of the creature
	 * and moves on to the next unfinished or invalid one.
	 * @return true if the step is valid and completed and if the process has 
	 * moved on.
	 * @throws IllegalStateException if the creature is in the 
	 * {@link InitStatus#REVIEW} or {@link InitStatus#COMPLETED} step.
	 */
	public boolean validateInitStep() throws IllegalStateException{
		switch(this.getInitialisationStatus()) {
		case ABILITIES:
			if(this.getTempAbilityScores().checkValidity().isEmpty()) {
				this.initStatus = InitStatus.REVIEW;
				return true;
			} else {
				return false;
			}
		case REVIEW:
		case COMPLETED:
			throw new IllegalStateException("Tried to validate the " + getInitialisationStatus() + 
					" step, which is illegal");
		default:
			throw new NotYetImplementedException();
		}
	}
}
