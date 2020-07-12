package model.values;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AbilityScoreStaticTest {

	/**
	 * Checks that {@link AbilityScore#computeModifier(int)} returns consistent
	 * results for the scores from 0 to 23.
	 * @param value		of the ability score.
	 * @param modifier	expected modifier for the ability score.
	 */
	@ParameterizedTest(name = "Value {0} has modifier {1}")
	@CsvSource({
		"0, -5","1, -5","2, -4","3, -4","4, -3", "5, -3", "6, -2", "7, -2", 
		"8, -1", "9, -1", "10, 0", "11, 0", "12, 1", "13, 1", "14, 2", "15, 2",
		"16, 3", "17, 3", "18, 4", "19, 4", "20, 5", "21, 5", "22, 6", "23, 6"
	})
	public void computeModifier_verifyValidValues(int value, int modifier) {
			assertEquals(modifier, 
					AbilityScore.computeModifier(value),
					"Modifiers must be conform to the rulebook");
	}
}
