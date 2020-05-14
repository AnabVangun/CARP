package service.parameters;
/**
 * Non-instanciable container for the global parameters of the system relative
 * to the creatures.
 * These are not made to be tweaked but only to be shared between the layers 
 * of the program.
 * @author TLM
 */
public class CreatureParameters {
	/** 
	 * The different methods to initialise ability scores.
	 */
	public static enum AbilityGenerationMethod{
		/**
		 * Roll 4d6 six times, discard lowest score and assign the results
		 * between the six ability scores. Its parameter is the number of 
		 * extra dice that will be discarded for each ability score: it must be
		 * above 0.
		 */
		STANDARD(AssignmentOrder.ROLL_BEFORE_ASSIGN, 1),
		/**
		 * Distribute a number of extra d6 between the six ability scores and 
		 * keep only the best three dice for each score. Its parameter is the 
		 * number of dice to distribute: it must be above 0.
		 */
		DICE_POOL(AssignmentOrder.ROLL_AFTER_ASSIGN, 6),
		/**
		 * Directly assign the values of the ability scores. It has no 
		 * parameter.
		 */
		DIRECT_ASSIGNMENT(AssignmentOrder.NO_ROLL, 0);
		
		private final AssignmentOrder order;
		private final int parameter;
		private AbilityGenerationMethod(AssignmentOrder order, int param) {
			this.order = order;
			this.parameter = param;
		}
		/**
		 * @return an {@link AssignmentOrder} constant indicating whether the 
		 * dice are rolled before or after mapping the ability scores with the
		 * dice sets in this method.
		 */
		public AssignmentOrder getOrder() {
			return this.order;
		}
		
		/**
		 * @return the parameter associated with the method: number of dice, 
		 * of points, etc. The exact meaning depends on the method.
		 */
		public int getParameter() {
			return this.parameter;
		}
	}
	
	/**
	 * Whether the dice are rolled before or after assigning the dice sets to
	 * the different ability scores.
	 * @author TLM
	 */
	public static enum AssignmentOrder{
		/**
		 * The method rolls the dice before mapping the ability scores with 
		 * the results.
		 */
		ROLL_BEFORE_ASSIGN,
		/**
		 * The method maps the ability scores with the dice sets and then rolls
		 * the dice.
		 */
		ROLL_AFTER_ASSIGN,
		/**
		 * The method does not include rolling dice.
		 */
		NO_ROLL;
	}
	/**
	 * Names of the six abilities.
	 * @author TLM
	 */
	public static enum AbilityName{
		STRENGTH,
		DEXTERITY,
		CONSTITUTION,
		INTELLIGENCE,
		WISDOM,
		CHARISMA;
	}
	private CreatureParameters() {};

}
