/**
 * 
 */
package model.values;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Container for a set of dice, grouped by number of sides.
 * @author TLM
 */
public class Roll {
	/** Set of dice included in the roll. */
	private Map<Integer, int[]> dice;
	/** State of the dice: true when the dice have been rolled. */
	private boolean rolled = false;
	/**
	 * RNG used for all the dice roll.
	 */
	private static Random rng = new Random();
	
	/**
	 * Initialises a simple set of identical dice.
	 * The set may contain at most {@link ValueParameters#MAX_NUMBER_OF_DICE} 
	 * dice, and each die may have at most 
	 * {@link ValueParameters#MAX_NUMBER_OF_SIDES} sides.
	 * @param numberOfDice included in the set
	 * @param numberOfSides of the dice in the set
	 */
	public Roll(int numberOfDice, int numberOfSides) {
		if(numberOfDice < ValueParameters.MIN_NUMBER_OF_DICE 
				|| numberOfSides < ValueParameters.MIN_NUMBER_OF_SIDES 
				|| numberOfDice > ValueParameters.MAX_NUMBER_OF_DICE 
				|| numberOfSides > ValueParameters.MAX_NUMBER_OF_SIDES) {
			throw new IllegalArgumentException(
					"There must be between " + ValueParameters.MIN_NUMBER_OF_DICE + " and "
					+ ValueParameters.MAX_NUMBER_OF_DICE + " dice (received " + 
					numberOfDice + ") with between " + ValueParameters.MIN_NUMBER_OF_SIDES
					+ " and " + ValueParameters.MAX_NUMBER_OF_SIDES + " sides (received " 
					+ numberOfSides + ").");
		}
		dice = new HashMap<Integer, int[]>();
		dice.put(numberOfSides, new int[numberOfDice]);
	}
	
	/**
	 * Rolls the dice.
	 * @return the sum of all dice.
	 */
	public int roll(){
		rolled = true;
		for(int sides : dice.keySet()) {
			for(int i = 0; i < dice.get(sides).length; i++) {
				dice.get(sides)[i] = rng.nextInt(sides) + 1;
			}
		}
		return computeScore();
	}
	
	/**
	 * Returns the results of the last roll of the dice set. Rolls the dice if
	 * it had not been done yet.
	 * @return an unmodifiable map with, for each number of sides, an array of 
	 * the result of
	 * the roll of each die.
	 */
	public Map<Integer, int[]> getResults(){
		if(!rolled) {
			roll();
		}
		return Collections.unmodifiableMap(this.dice);
	}

	
	/**
	 * Returns the total result of the last roll of the dice set. Rolls the 
	 * dice if
	 * it had not been done yet.
	 * @return the sum of all the dice in the roll.
	 */
	public int getScore() {
		if(!rolled) {
			return roll();
		}
		return computeScore();
	}
	
	/**
	 * Seeds the random number generator used to roll the dice.
	 * @param seed
	 */
	public static void seed(Long seed) {
		rng = new Random(seed);
	}
	
	/**
	 * Computes the total value of the roll.
	 * @return	the sum of all the dice in the roll.
	 */
	private int computeScore() {
		int result = 0;
		for(int[] results : dice.values()) {
			for(int i:results) {
				result += i;
			}
		}
		return result;
	}

}
