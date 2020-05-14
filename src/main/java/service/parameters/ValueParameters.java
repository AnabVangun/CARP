package service.parameters;

/**
 * Non-instanciable container for the global parameters of the system relative
 * to the values.
 * These are not made to be tweaked but only to be shared between the layers 
 * of the program.
 * @author TLM
 */
public final class ValueParameters {

	/**Prevents the class from being instantiated.*/
	private ValueParameters() {}
	/**Private parameter to set a maximum for the input values.*/
	private static final int MAX_INT_VALUE = 999;
	/**Minimum valid value for an ability score. Must be 0.*/
	public static final int MIN_ABILITY_SCORE = 0;
	/**Maximum valid value for an ability score. Must be at least 40.*/
	public static final int MAX_ABILITY_SCORE = MAX_INT_VALUE;
	/**Minimum valid number of dice in a roll. Must be 1.*/
	public static final int MIN_NUMBER_OF_DICE = 1;
	/**Minimum valid number of sides for a die. Must be at most 4.*/
	public static final int MIN_NUMBER_OF_SIDES = 2;
	/**Maximum valid number of dice in a roll. Must be at least 10.*/
	public static final int MAX_NUMBER_OF_DICE = MAX_INT_VALUE;
	/**Maximum valid number of sides for a dice. Must be at least 100.*/
	public static final int MAX_NUMBER_OF_SIDES = MAX_INT_VALUE;
}
