package model.exceptions;

/**
 * Exception raised when an object tries to create an illegal 
 * {@link model.values.AbilityScore} or {@link model.creatures.AbilityScores}.
 * @author TLM
 */
public class IllegalAbilityScoreException extends RuntimeException {
	private static final long serialVersionUID = 6846962934628169718L;
	
	public static enum Cause{
		NULL,
		UNDEFINED
	}

	/**
	 * Initialises an {@link IllegalAbilityScoreException} for when an illegal
	 * value is given to the constructor.
	 * @param ability	name of the ability with an illegal value.
	 * @param value		value in the illegal call to the AbilityScore
	 * constructor.
	 */
	public IllegalAbilityScoreException(String ability, int value){
		super("An ability score must be positive or null, received " + value 
				+ "for ability " + ability + ".");
	}
	
	/**
	 * Initialises an {@link IllegalAbilityScoreException} for when a required
	 * ability is not initialised.
	 * @param missingAbility
	 * @param listOfAbilities
	 */
	public IllegalAbilityScoreException(String missingAbility, String listOfAbilities) {
		super("Ability score " + missingAbility 
				+ " must be defined, received no value for it in map with " 
				+ listOfAbilities + ".");
	}
	
	/**
	 * Initialises an {@link IllegalAbilityScoreException} for when an illegal 
	 * call to {@link model.values.Value#getValue()} is performed on the UNDEFINED
	 * AbilityScore or when an {@link model.creatures.AbilityScores} object is 
	 * initialised with a null input.
	 */
	public IllegalAbilityScoreException(Cause type) {
		super(pickMessage(type));
	}
	
	/**
	 * Picks the message to set in the 
	 * {@link IllegalAbilityScoreException#IllegalAbilityScoreException(Cause)}
	 * constructor.
	 * @param cause of the issue raising the exception
	 * @return the adequate message for the given situation
	 */
	static private String pickMessage(Cause cause) {
		String message;
		switch(cause) {
		case NULL:
			message = "Tried to create an AbilityScores object with a null input";
			break;
		case UNDEFINED:
			message = "Tried to call getValue() on the UNDEFINED AbilityScore.";
			break;
		default:
			message = "An unknown error has occurred";
		}
		return message;
	}
}
