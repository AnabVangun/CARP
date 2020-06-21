package viewmodel.tools;
/**
 * Non-instanciable container for the global parameters of the system relative
 * to the view models.
 * These are not made to be tweaked but only to be shared between the layers 
 * of the program.
 * @author TLM
 *
 */
public class ViewModelParameters {
	private ViewModelParameters() {}
	/**
	 * List of generic styles available in the main resource bundle
	 * to maintain a consistent look and feel throughout the application.
	 * These are not exclusive and can be combined.
	 * @author TLM
	 */
	public enum Styles{
		/** Used to put a strong emphasis on a element.*/
		STRONG_EMPHASIS,
		/**Used to style editable objects.*/
		EDITABLE,
		/**Used to style valid user inputs.*/
		VALID,
		/**Used to style invalid user inputs.*/
		INVALID,
		/**
		 * Used to style temporary values that are better than the current 
		 * ones.
		 */
		BETTER,
		/**
		 * Used to style temporary values that are worse than the current 
		 * ones.
		 */
		WORSE,
		/**
		 * Used to style temporary values that are absent when there isn't
		 * already a value.
		 */
		NONE,
		/**
		 * Used to style temporary values that are absent when there already is
		 * a value.
		 */
		NULLIFY;
	}
}
