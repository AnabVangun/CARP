/**
 * 
 */
package model.values;

/**
 * Read-only container for one of the six ability scores of a creature: 
 * Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma.
 * Implementations of this interface MUST override 
 * {@link Object#equals(Object)} and {@link Object#hashCode()} so that two 
 * objects containing the same value are equals, undefined abilities are only
 * equal to other undefined abilities, and equal abilities have the same 
 * hashCode.
 * @author TLM
 */
public interface AbilityScore extends Comparable<AbilityScore>{
	
	/**
	 * @return the modifier of the ability score. There is no need to check if
	 * the ability score is properly defined before calling this method, 
	 * implementations must handle gracefully this case.
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
	 * @return	the value of the ability score, taking all bonuses into 
	 * account.
	 * @throws UnsupportedOperationException if the ability is not defined for 
	 * the creature as per {@link AbilityScore#isDefined()}.
	 */
	public int getValue();
	
	/**
	 * @return	true if and only if the ability is defined for the creature.
	 */
	public boolean isDefined();

	@Override
	public default int compareTo(AbilityScore o) {
		//Undefined is worse than anything except undefined
		if(!this.isDefined()) {
			return o.isDefined() ? -1 : 0;
		} else if (!o.isDefined()) {
			return 1;
		}
		return this.getValue() - o.getValue();
	}
}
