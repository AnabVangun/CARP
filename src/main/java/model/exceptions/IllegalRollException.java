package model.exceptions;

import service.parameters.ValueParameters;

/**
 * Exception raised when an object tries to create an illegal {@link Roll}:
 * less than one die, less than two sides, or more than 999 dice or sides.
 * @author TLM
 */
public class IllegalRollException extends IllegalArgumentException {
	private static final long serialVersionUID = -8518498605686048527L;
	
	/**
	 * Simple constructor for an {@link IllegalRollException}.
	 * @param numberOfDice	number of dice in the illegal call to the Dice
	 * constructor
	 * @param numberOfSides	number of sides in the illegal call to the Dice
	 * constructor
	 */
	public IllegalRollException(int numberOfDice, int numberOfSides){
		super("There must be between " + ValueParameters.MIN_NUMBER_OF_DICE + " and "
				+ ValueParameters.MAX_NUMBER_OF_DICE + " dice (received " + 
				numberOfDice + ") with between " + ValueParameters.MIN_NUMBER_OF_SIDES
				+ " and " + ValueParameters.MAX_NUMBER_OF_SIDES + " sides (received " 
				+ numberOfSides + ").");
	}
}
