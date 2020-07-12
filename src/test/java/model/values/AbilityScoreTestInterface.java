package model.values;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Interface verifying that a class implementing the {@link AbilityScore} 
 * interface respects the contracts set by the method descriptions.
 * @author TLM
 */
public interface AbilityScoreTestInterface {
	/**
	 * Generates an {@link AbilityScore} object to run the tests.
	 * @param value		to store in the ability
	 * @param isDefined	true if the new ability must be defined.
	 * @return	an {@link AbilityScore} object storing the input value.
	 */
	AbilityScore createAbilityScore(Integer value, boolean isDefined);
	
	static Arguments[] abilityScoreParametersSupplier() {
		return new Arguments[] {Arguments.of(5, true),
				Arguments.of(15, true),
				Arguments.of(5, false),
				Arguments.of(25, false),
				Arguments.of(ValueParameters.MIN_ABILITY_SCORE - 3, true),
				Arguments.of(ValueParameters.MAX_ABILITY_SCORE + 6, true)
		};
	}
	
	static Arguments[] differentAbilityScoreParametersSupplier() {
		return new Arguments [] {Arguments.of(1, true, 5),
				Arguments.of(15, true, 18),
				Arguments.of(ValueParameters.MIN_ABILITY_SCORE, true, ValueParameters.MAX_ABILITY_SCORE),
				Arguments.of(5, false, 3),
				Arguments.of(ValueParameters.MAX_ABILITY_SCORE + 4, false, ValueParameters.MIN_ABILITY_SCORE - 7),
				Arguments.of(ValueParameters.MIN_ABILITY_SCORE - 2, true, 10),
				Arguments.of(ValueParameters.MIN_ABILITY_SCORE - 13, true, ValueParameters.MAX_ABILITY_SCORE + 2)};
	}
	
	/**
	 * Checks that {@link AbilityScore#isDefined()} returns true if and only if
	 * the ability is properly defined.
	 * @param value		valid value for an ability
	 * @param isDefined	true if the ability is expected to be defined
	 */
	@ParameterizedTest(name = "Is defined on value {0}, defined {1}")
	@MethodSource("abilityScoreParametersSupplier")
	default void isDefined_verifyValue(Integer value, boolean isDefined) {
		assertEquals(isDefined, createAbilityScore(value, isDefined).isDefined());
	}
	
	/**
	 * Checks that {@link AbilityScore#getValue()} returns the value of the
	 * ability if it is properly defined, or throws an exception otherwise.
	 * @param value
	 * @param isDefined
	 */
	@ParameterizedTest(name = "Get value on value {0}, defined {1}")
	@MethodSource("abilityScoreParametersSupplier")
	default void getValue_VerifyValue(Integer value, boolean isDefined) {
		AbilityScore ability = createAbilityScore(value, isDefined);
		if(isDefined) {
			assertEquals(value, ability.getValue());
		} else {
			assertThrows(UnsupportedOperationException.class, () -> ability.getValue());
		}
	}

	/**
	 * Checks that {@link AbilityScore#getModifier()} computes the modifier of 
	 * the ability as per {@link AbilityScore#computeModifier(int)} if the 
	 * ability is properly defined.
	 * @param value
	 * @param isDefined
	 */
	@ParameterizedTest(name = "Get modifier on value {0}, defined {1}")
	@MethodSource("abilityScoreParametersSupplier")
	default void getModifier_VerifyValue(Integer value, boolean isDefined) {
		AbilityScore ability = createAbilityScore(value, isDefined);
		assertEquals(isDefined?AbilityScore.computeModifier(value):0,
				ability.getModifier());
	}
	
	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} is <0 if
	 * the object is strictly lower than the argument, and >0 if it is
	 * strictly higher than the argument, considering that an undefined
	 * ability is lower than any defined ability. Given that the comparison is
	 * based on the natural order of the values of the ability scores with
	 * undefined abilities being considered as "MIN_ABILITY_SCORE - 1", 
	 * transitivity is ensured as long as this test succeeds.
	 * @param value1		Value of the first ability score.
	 * @param isDefined1	Whether the first ability score is defined
	 * @param value2		Value of the second ability score, which must be 
	 * higher than the first.
	 */
	@ParameterizedTest(name = "Compare {0} (defined: {1}) and {2} (defined: true)")
	@MethodSource("differentAbilityScoreParametersSupplier")
	default void compareTo_verifySignOnDifferentValues(Integer value1, boolean isDefined1,
			Integer value2) {
		AbilityScore lesser = createAbilityScore(value1, isDefined1);
		AbilityScore higher = createAbilityScore(value2, true);
		assertAll(() -> assertTrue(lesser.compareTo(higher) < 0, 
						() -> "Expected negative value, received " + lesser.compareTo(higher)),
				() -> assertTrue(higher.compareTo(lesser) > 0,
						() -> "Expected positive value, received " + higher.compareTo(lesser))
				);
	}
	
	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} is ==0 if the 
	 * object is equal to the argument, considering that undefined
	 * abilities are equal independently of their values. Also checks that
	 * if two abilities are equal, the results of their comparison with a third
	 * ability have the same sign.
	 * @param value1		Value of the first ability score.
	 * @param isDefined		Whether the first two abilities are defined.
	 * @param value2		Value of the second ability score. Expected to be
	 * equal to value1, unless isDefined is false.
	 * @param value3		Value of the third ability score.
	 * @param isDefined3	Whether the third ability score is defined.
	 */
	@ParameterizedTest(name = "Comparison equality between {0} and {2} (defined: {1}) and"
			+ " comparison with {3} (defined {4})")
	@CsvSource({"2, true, 2, 6, true", "13, true, 13, 5, true", "10, true, 10, 10, true",
		"4, true, 4, 7, false", "5, false, 23, 4, false"})
	default void compareTo_Equality(Integer value1, boolean isDefined,
			Integer value2, Integer value3, boolean isDefined3) {
		AbilityScore first = createAbilityScore(value1, isDefined);
		AbilityScore second = createAbilityScore(value2, isDefined);
		AbilityScore third = createAbilityScore(value3, isDefined3);
		assertAll(() -> assertEquals(0, first.compareTo(second)),
				() -> assertEquals(0, second.compareTo(first)),
				() -> {
					if(first.compareTo(third) == 0) {
						assertEquals(0, second.compareTo(third));
					} else {
						assertTrue(first.compareTo(third) * second.compareTo(third) > 0,
								() -> first.compareTo(third) + " and " + second.compareTo(third) 
								+ " should have the same sign");
					}
				});
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return true when used on
	 * two objects containing the same value with the same definition status.
	 * @param value
	 * @param isDefined
	 */
	@ParameterizedTest(name = "Equality on {0} (defined {1})")
	@MethodSource("abilityScoreParametersSupplier")
	default void equality_Equality(Integer value, boolean isDefined) {
		AbilityScore first = createAbilityScore(value, isDefined);
		AbilityScore second = createAbilityScore(value, isDefined);
		assertEquals(first, second);
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return false when used 
	 * on two objects containing different values or with different definition 
	 * status.
	 * @param value1		Value of the first ability score.
	 * @param isDefined1	Whether the first ability score is defined
	 * @param value2		Value of the second ability score, which must be 
	 * higher than the first.
	 */
	@ParameterizedTest(name = "Equality between {0} (defined: {1}) and {2}")
	@MethodSource("differentAbilityScoreParametersSupplier")
	default void equality_Difference(Integer value1, boolean isDefined1,
			Integer value2) {
		AbilityScore first = createAbilityScore(value1, isDefined1);
		AbilityScore second = createAbilityScore(value2, true);
		assertNotEquals(first, second);
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return false when its
	 * input is an integer and not an {@link AbilityScore}.
	 * @param value
	 * @param isDefined
	 */
	@SuppressWarnings("unlikely-arg-type")
	@ParameterizedTest(name = "Equality (difference) on {0} (defined {1})")
	@MethodSource("abilityScoreParametersSupplier")
	default void equality_NotAnAbility(Integer value, boolean isDefined) {
		AbilityScore first = createAbilityScore(value, isDefined);
		Integer second = isDefined ? value : null;
		assertFalse(first.equals(second));
	}
	/**
	 * Checks that {@link AbilityScore#hashCode()} return the same result when 
	 * used several times on the same object.
	 * @param value
	 * @param isDefined
	 */
	@ParameterizedTest(name = "Equality on {0} (defined {1})")
	@MethodSource("abilityScoreParametersSupplier")
	default void hashCode_consistentResult(Integer value, boolean isDefined) {
		AbilityScore score = createAbilityScore(value, isDefined);
		int first = score.hashCode();
		int second = score.hashCode();
		assertEquals(first, second);
	}
	/**
	 * Checks that {@link AbilityScore#hashCode()} return the same result when 
	 * used on two equal {@link AbilityScore}.
	 * @param value
	 * @param isDefined
	 */
	@ParameterizedTest(name = "Equality on {0} (defined {1})")
	@MethodSource("abilityScoreParametersSupplier")
	default void hashCode_Equality(Integer value, boolean isDefined) {
		AbilityScore first = createAbilityScore(value, isDefined);
		AbilityScore second = createAbilityScore(value, isDefined);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
