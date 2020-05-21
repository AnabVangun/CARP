/**
 * 
 */
package model.creatures;

/**
 * Actor in the game. Can be a player character, a non player character, or a 
 * monster.
 * @author TLM
 */
public class Creature {
	RWAbilityScores abilities;

	/**
	 * Initialise an empty creature.
	 * After this constructor has been called, the creature is not fully
	 * initialised yet: it needs ability scores, a race, optionally class
	 * levels, skill ranks,
	 * feats, equipment, and finishing details.
	 */
	public Creature() {}
	
	/**
	 * Sets the creature's {@link AbilityScores} and propagates the modifiers
	 * wherever applicable.
	 * @param abilities valid set of abilities. A deep copy is made.
	 */
	public void setAbilityScores(AbilityScores abilities) {
		this.abilities = new RWAbilityScores(abilities);
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

}
