/**
 * 
 */
package model.values;

/**
 * Read-only container for one of the six ability scores of a creature: 
 * Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma.
 * @author TLM
 */
public interface AbilityScore {
	
	/**
	 * @return the modifier of the ability score.
	 */
	public int getModifier();
	
	/**
	 * Computes the modifier associated with a given value for an ability 
	 * score.
	 * @param value	to take into account.
	 * @return the modifier associated with the given value.
	 */
	public static int computeModifier(int value) {
		return value/2 - 5;
	}
	
	/**
	 * @return	the value of the ability score, taking all bonuses into account.
	 */
	public int getValue();

}
