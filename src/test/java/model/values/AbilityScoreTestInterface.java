package model.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import tools.TestFrameWork;

/**
 * Interface verifying that a class implementing the {@link AbilityScore} 
 * interface respects the contracts set by the method descriptions.
 * @author TLM
 */
public interface AbilityScoreTestInterface<T extends AbilityScoreArguments> 
extends TestFrameWork<AbilityScore, T>{
	/**
	 * Generates the arguments for an {@link AbilityScore} object to run the 
	 * tests.
	 * @param value		to store in the ability
	 * @param isDefined	true if the new ability must be defined.
	 * @return	an {@link AbilityScore} object storing the input value.
	 */
	T createAbilityScoreArgument(Integer value, boolean isDefined);

	@Override
	default Stream<T> argumentsSupplier() {
		return Stream.of(createAbilityScoreArgument(5, true),
				createAbilityScoreArgument(15, true),
				createAbilityScoreArgument(5, false),
				createAbilityScoreArgument(25, false),
				createAbilityScoreArgument(ValueParameters.MIN_ABILITY_SCORE - 3, true),
				createAbilityScoreArgument(ValueParameters.MAX_ABILITY_SCORE + 6, true));
	}
	
	default Map<T, T> differentAbilityScoreParametersSupplier() {
		Map<T, T> result = new HashMap<>();
		result.put(createAbilityScoreArgument(1, true), createAbilityScoreArgument(5, true));
		result.put(createAbilityScoreArgument(15, true), createAbilityScoreArgument(18, true));
		result.put(createAbilityScoreArgument(ValueParameters.MIN_ABILITY_SCORE, true), 
				createAbilityScoreArgument(ValueParameters.MAX_ABILITY_SCORE, true));
		result.put(createAbilityScoreArgument(5, false), createAbilityScoreArgument(3, true));
		result.put(createAbilityScoreArgument(ValueParameters.MAX_ABILITY_SCORE + 4, false), 
				createAbilityScoreArgument(ValueParameters.MIN_ABILITY_SCORE - 7, true));
		result.put(createAbilityScoreArgument(ValueParameters.MIN_ABILITY_SCORE - 2, true), 
				createAbilityScoreArgument(10, true));
		result.put(createAbilityScoreArgument(ValueParameters.MIN_ABILITY_SCORE - 13, true), 
				createAbilityScoreArgument(ValueParameters.MAX_ABILITY_SCORE + 2, true));
		return result;
	}
	
	@Override
	default String testName(String method, T first) {
		return testName(method, first, null, null);
	}
	
	default String testName(String method, T first, T second) {
		return testName(method, first, second, null);
	}
	/**
	 * Forges the name of the test case based on one to three 
	 * {@link AbilityScore} objects.
	 * @param method	under test
	 * @param args		the one to three arguments to build the corresponding
	 * {@link AbilityScore} objects.
	 * @return	a String containing the name of the test and the description of
	 * the {@link AbilityScore} object used for the test.
	 */
	default String testName(String method, T first, T second, T third) {
		if(first != null && second == null && third == null) {
			return String.format("AbilityScore.%s on value %s", method, first);
		} else if (first != null && second != null && third == null) {
			return String.format("AbilityScore.%s on values %s and %s", 
					method, first, second); 
		} else if (first != null && second != null && third != null) {
			return String.format("AbilityScore.%s on values %s, %s and %s", method, first, second, third);
		} else {
			throw new IllegalArgumentException(String.format("This method received %s, %s and %s",
					first, second, third));
		}
	}
	
	/**
	 * Checks that {@link AbilityScore#isDefined()} returns true if and only if
	 * the ability is properly defined.
	 */
	@TestFactory
	default Stream<DynamicTest> isDefined_verifyValue() {
		return test("isDefined()", args
				-> assertEquals(args.isDefined, args.convert().isDefined()));
	}
	/**
	 * Checks that {@link AbilityScore#getValue()} returns the value of the
	 * ability if it is properly defined, or throws an exception otherwise.
	 */
	@TestFactory
	default Stream<DynamicTest> getValue_VerifyValue() {
		return test("getValue()", args
				-> getValue_VerifyValue_AssertSwitch(args.isDefined, args.convert(), args.value));
	}
	/**
	 * Checks that {@link AbilityScore#getValue()} returns the value of the
	 * ability if it is properly defined, or throws an exception otherwise.
	 * @param isDefined	true if the ability is supposed not to be defined
	 * @param ability	ability to test
	 * @param value		expected value if the ability is defined
	 */
	default void getValue_VerifyValue_AssertSwitch(boolean isDefined, AbilityScore ability, Integer value) {
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
	 */
	@TestFactory
	default Stream<DynamicTest> getModifier_VerifyValue() {
		return test("getModifier()", args
				-> assertEquals(args.isDefined?AbilityScore.computeModifier(args.value):0,
						args.convert().getModifier()));
	}
	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} is <0 if
	 * the object is strictly lower than the argument, and >0 if it is
	 * strictly higher than the argument, considering that an undefined
	 * ability is lower than any defined ability. Given that the comparison is
	 * based on the natural order of the values of the ability scores with
	 * undefined abilities being considered as "MIN_ABILITY_SCORE - 1", 
	 * transitivity is ensured as long as this test succeeds.
	 */
	@TestFactory
	default Stream<DynamicNode> compareTo_verifySignOnDifferentValues() {
		return differentAbilityScoreParametersSupplier().entrySet().stream().map(entry 
				-> compareTo_verifySignOnDifferentValues(
						entry.getKey(), entry.getValue()));
	}
	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} is <0 if
	 * the object is strictly lower than the argument, and >0 if it is
	 * strictly higher than the argument, considering that an undefined
	 * ability is lower than any defined ability.
	 * @param lesserArg	the arguments for the lesser {@link AbilityScore}
	 * @param higherArg	the arguments for the higher {@link AbilityScore}
	 * @return a container checking that 
	 * {@link AbilityScore#compareTo(AbilityScore)} returns the expected 
	 * result.
	 */
	default DynamicNode compareTo_verifySignOnDifferentValues(T lesserArg, 
			T higherArg) {
		String message = testName("compareTo", lesserArg, higherArg);
		AbilityScore lesser = lesserArg.convert();
		AbilityScore higher = higherArg.convert();
		return dynamicContainer(message, Stream.of(
				dynamicTest(message + ": lesser < higher", () -> assertTrue(lesser.compareTo(higher) < 0, 
						() -> "Expected negative value, received " + lesser.compareTo(higher))),
				dynamicTest(message + ": higher > lesser", () -> assertTrue(higher.compareTo(lesser) > 0,
						() -> "Expected positive value, received " + higher.compareTo(lesser)))));
	}
	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} == 0 if the 
	 * object is equal to the argument and that
	 * if two abilities are equal, the results of their comparison with a third
	 * ability have the same sign.
	 * @return	a stream of unit tests each containing one assertion.
	 */
	@TestFactory
	default Stream<DynamicNode> compareTo_Equality() {
		Stream<Stream<T>> args = Stream.of(
				Stream.of(createAbilityScoreArgument(2, true), 
						createAbilityScoreArgument(2, true), 
						createAbilityScoreArgument(6, true)),
				Stream.of(createAbilityScoreArgument(13, true), 
						createAbilityScoreArgument(13, true), 
						createAbilityScoreArgument(5, true)),
				Stream.of(createAbilityScoreArgument(10, true), 
						createAbilityScoreArgument(10, true), 
						createAbilityScoreArgument(10, true)),
				Stream.of(createAbilityScoreArgument(4, true), 
						createAbilityScoreArgument(4, true), 
						createAbilityScoreArgument(7, false)),
				Stream.of(createAbilityScoreArgument(5, false), 
						createAbilityScoreArgument(23, false), 
						createAbilityScoreArgument(4, false))
				);
		/*
		 * XXX find a nicer way to bypass the inability to create T[].
		 */
		return args.map(stream -> {
			Iterator<T> iter = stream.iterator();
			return compareTo_Equality(iter.next(), iter.next(), iter.next());
		});
	}

	/**
	 * Checks that {@link AbilityScore#compareTo(AbilityScore)} == 0 if the 
	 * object is equal to the argument and that
	 * if two abilities are equal, the results of their comparison with a third
	 * ability have the same sign.
	 * @param args	the three abilities to compare. The first two are supposed
	 * to be equal as far as {@link AbilityScore#compareTo(AbilityScore)} is 
	 * concerned.
	 * @return a stream of unit tests each containing one assertion.
	 */
	default DynamicNode compareTo_Equality(T firstArg, T secondArg, T thirdArg) {
		String name = testName("compareTo (equality)", firstArg, secondArg, thirdArg);
		AbilityScore first = firstArg.convert();
		AbilityScore second = secondArg.convert();
		AbilityScore third = thirdArg.convert();
		return dynamicContainer(name, Stream.of(
				dynamicTest(name + ": a equals b", () -> assertEquals(0, first.compareTo(second))),
				dynamicTest(name + ": b equals a", () -> assertEquals(0, second.compareTo(first))),
				dynamicTest(name + ": a equals b so a.compareTo(c)~b.compareTo(c)",
						() -> {
							if(first.compareTo(third) == 0) {
								assertEquals(0, second.compareTo(third));
							} else {
								assertTrue(first.compareTo(third) * second.compareTo(third) > 0,
										() -> first.compareTo(third) + " and " + second.compareTo(third) 
										+ " should have the same sign");
								}
						}
							)));
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return true when used on
	 * two objects containing the same value with the same definition status.
	 */
	@TestFactory
	default Stream<DynamicTest> equality_Equality() {
		return test("equals() (equality case)", input
				-> assertEquals(input.convert(),
						input.convert()));
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return false when used 
	 * on two objects containing different values or with different definition 
	 * status.
	 */
	@TestFactory
	default Stream<DynamicTest> equality_Difference() {
		return differentAbilityScoreParametersSupplier().entrySet().stream().map(entry
				-> dynamicTest(testName("equals (difference case)", entry.getKey(), entry.getValue()),
						() -> assertNotEquals(entry.getKey().convert(),
								entry.getValue().convert())));
	}
	/**
	 * Checks that {@link AbilityScore#equals(Object)} return false when its
	 * input is an integer and not an {@link AbilityScore}.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@TestFactory
	default Stream<DynamicTest> equality_NotAnAbility() {
		return test("equals() (heterogeneous case)", args
				-> assertFalse(args.convert().equals(args.isDefined ? args.value : null)));
	}
	/**
	 * Checks that {@link AbilityScore#hashCode()} return the same result when 
	 * used several times on the same object.
	 */
	@TestFactory
	default Stream<DynamicTest> hashCode_consistentResult() {
		return test("hashCode() (consistent results)", args
				-> {
					AbilityScore score = args.convert();
					assertEquals(score.hashCode(), score.hashCode());
		});
	}
	/**
	 * Checks that {@link AbilityScore#hashCode()} return the same result when 
	 * used on two equal {@link AbilityScore}.
	 */
	@TestFactory
	default Stream<DynamicTest> hashCode_Equality() {
		return test("hashCode() (equality)", args
				-> assertEquals(args.convert().hashCode(), args.convert().hashCode()));
	}
}