package model.values;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class RollTest {
	/*
	 * XXX This class only tests with one type of dice.
	 * When Roll handle several types of dice at the same time, 
	 * this method will have to be adapted.
	 * XXX This class does not take bonuses into account. When Roll handle
	 * them, this method will have to be adapted.
	 * XXX When Junit5 supports repeated parameterized tests, this should 
	 * be simplified.
	 */
	public static final long SEED = (long) 2;
	private static final int REPEAT = 100;
	//Short hands for the parameter related to rolls
	private static final int MAX_DICE = ValueParameters.MAX_NUMBER_OF_DICE;
	private static final int MAX_SIDES = ValueParameters.MAX_NUMBER_OF_SIDES;
	private static final int MIN_DICE = ValueParameters.MIN_NUMBER_OF_DICE;
	private static final int MIN_SIDES = ValueParameters.MIN_NUMBER_OF_SIDES;
	
	/**
	 * @return	valid sets of dice to test the {@link Roll} class.
	 */
	private static Arguments[] diceSetsSupplier () {
		return new Arguments[]{Arguments.of(1, 6), 
				Arguments.of(3, 20), 
				Arguments.of(7, 7),
				Arguments.of(MAX_DICE, MAX_SIDES),
				Arguments.of(MIN_DICE, MIN_SIDES)};
	}
	
	@BeforeEach
	void setUp() {
		Roll.seed(SEED);
	}

	/**
	 * Checks that {@link Roll#Roll(int, int)} builds a {@link Roll} object
	 * without raising an exception on valid input.
	 * @param numberOfDice	valid value for the number of dice in the set.
	 * @param numberOfSides	valid value for the number of sides of the dice.
	 */
	@ParameterizedTest(name = "{0}d{1} should work")
	@MethodSource("diceSetsSupplier")
	public void rollConstructor_ValidValues(int numberOfDice, int numberOfSides) {
		assertDoesNotThrow(() -> new Roll(numberOfDice, numberOfSides));
	}
	/**
	 * Checks that {@link Roll#Roll(int, int)} throws an 
	 * {@link IllegalArgumentException} exception on invalid input.
	 * @param numberOfDice	invalid value for the number of dice in the set.
	 * @param numberOfSides	invalid value for the number of sides of the dice.
	 */
	@ParameterizedTest(name = "{0}d{1} should fail")
	@CsvSource({(MIN_DICE - 4) + ", 6", (MIN_DICE - 1) + ", 13",
		"1, " + (MIN_SIDES - 3), "5, " + (MIN_SIDES - 1),
		(MIN_DICE - 1) + ", " + (MIN_SIDES - 1), (MAX_DICE + 1) + ", 6",
		"5, " + (MAX_SIDES + 1), (MAX_DICE + 25) + ", " + (MAX_SIDES + 3)
		})
	public void rollConstructor_failOnInvalidValues(int numberOfDice, int numberOfSides) {
		assertThrows(IllegalArgumentException.class, () -> new Roll(numberOfDice, numberOfSides));
	}
	/**
	 * Checks that {@link Roll#roll()} returns the sum of the values of the
	 * individual dice in the set as returned by {@link Roll#getResults()}.
	 * @param numberOfDice	valid value for the number of dice in the set.
	 * @param numberOfSides	valid value for the number of sides of the dice.
	 */
	@ParameterizedTest(name = "Sum of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	public void roll_SumConsistentWithResults(int dice, int sides) {
		Executable tester = () -> {
			Roll roll = new Roll(dice, sides);
			int directResult = roll.roll();
			int expectedSum = 0;
			for(int i : roll.getResults().get(sides)) {
				expectedSum += i;
			}
			assertEquals(expectedSum, directResult,
					() -> "The result of throwing " + dice + "d" + sides 
					+ "is the sum of the value of each die");
		};
		assertAll(Arrays.stream(new int[REPEAT]).mapToObj((i) -> tester));
	}

	/**
	 * Checks that the {@link Roll#getResults()} method returns results 
	 * consistent with the dice.
	 */
	@ParameterizedTest(name = "Results of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	public void getResults_ConsistentWithDiceParams(int dice, int sides) {
		/** Checks that i is an acceptable value for a dice roll result. */
		Function<Integer, Executable> boundsChecker = (Integer i) -> {
			return () -> {
				assertAll(() -> assertTrue(i <= sides, 
						"Rolling " + dice + "d" + sides + " produced " + i),
						() -> assertTrue (i > 0,
						"Rolling " + dice + "d" + sides + " produced " + i));
			};
		};
		/** Checks that rolling the dice set only produces valid results. */
		Executable tester = () -> {
			Roll tested = new Roll(dice, sides);
			tested.roll();
			Integer[] results = tested.getResults().get(sides);
			Stream<Integer> resultsStream = Arrays.stream(results);
			assertAll(
					() -> assertEquals(dice, results.length,
							"Results must contain as many entries as there are dice"),
					() -> assertAll(resultsStream.map(boundsChecker))
					);
		};
		assertAll(Arrays.stream(new int[REPEAT]).mapToObj((i) -> tester));
	}
	
	/**
	 * Checks that seeding the {@link Roll} class seeds the global RNG used by
	 * all instances of the class.
	 */
	@Test
	public void seed_seedsRNG() {
		int numberOfSides = 6;
		Roll first = new Roll(1, numberOfSides);
		Roll second = new Roll(1, numberOfSides);
		Roll pair = new Roll(2, numberOfSides);
		Roll.seed(SEED);
		Integer[] resultSeparate = new Integer[] {first.roll(), second.roll()};
		Roll.seed(SEED);
		pair.roll();
		Integer[] resultPair = pair.getResults().get(numberOfSides);
		assertAll(() -> assertEquals(resultSeparate[0], resultPair[0]),
				() -> assertEquals(resultSeparate[1], resultPair[1]));
	}
	
	/**
	 * Checks that {@link Roll#getResults()} does not roll the dice and returns
	 * an array of null Integer for each number of sides in the set.
	 * @param dice
	 * @param sides
	 */
	@ParameterizedTest(name = "Results of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	void getResults_DoesNotRollTheDice(int dice, int sides) {
		Roll noRoll = new Roll(dice, sides);
		Map<Integer, Integer[]> noRollResults = noRoll.getResults();
		Roll.seed(SEED);
		Map<Integer, Integer[]> nullResults = new HashMap<>();
		nullResults.put(sides, new Integer[dice]);
		contentEqualityChecker(noRollResults, nullResults, true);
	}

	/**
	 * Checks that different calls to {@link Roll#getResults()} return maps 
	 * that contain the same results even if the dice are rolled in between.
	 * @param dice
	 * @param sides
	 */
	@ParameterizedTest(name = "Results of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	void getResults_EqualMaps(int dice, int sides) {
		Roll roll = new Roll(dice, sides);
		Map<Integer, Integer[]> firstResults = roll.getResults();
		roll.roll();
		Map<Integer, Integer[]> secondResults = roll.getResults();
		contentEqualityChecker(firstResults, secondResults, true);
	}
	
	/**
	 * Checks that the map returned by {@link Roll#getResults()} change when 
	 * the dice are rolled, unless they are equal by chance.
	 * @param dice
	 * @param sides
	 */
	@ParameterizedTest(name = "Results of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	void getResults_ResultsChangeWhenDiceRolled(int dice, int sides) {
		assumeTrue(sides >= 1, 
				"There must be more than one side for this test to make sense");
		Roll roll = new Roll(dice, sides);
		int firstSum = roll.roll();
		Map<Integer, Integer[]> firstResults = roll.getResults();
		Map<Integer, Integer[]> deepCopy = new HashMap<>();
		for(Map.Entry<Integer, Integer[]> entry : firstResults.entrySet()) {
			deepCopy.put(entry.getKey(), (Integer[]) Arrays.asList(entry.getValue()).toArray());
		}
		int secondSum = firstSum;
		while (secondSum == firstSum) {
			secondSum = roll.roll();
		}
		Map<Integer, Integer[]> secondResults = roll.getResults();
		//GetResults cannot be equal if their sums are different
		contentEqualityChecker(deepCopy, secondResults, false);
	}
	
	/**
	 * Verifies that the two input map have equals arrays associated to each 
	 * key.
	 * @param first
	 * @param second
	 */
	private final void contentEqualityChecker(Map<Integer, Integer[]> first, Map<Integer, Integer[]> second,
			boolean equality) {
		Stream<Integer> keys = first.keySet().stream();
		Consumer<Integer> tester = (i) -> {
			if(equality) {
				assertArrayEquals(first.get(i), second.get(i));
			} else {
				assertThrows(AssertionError.class, () -> assertArrayEquals(first.get(i), second.get(i)));
			}
		};
		assertAll(() -> assertEquals(first.keySet(), second.keySet()),
				() -> assertAll(keys.map((i) -> () -> tester.accept(i))));
	}
	

	/**
	 * Checks that the map returned by {@link Roll#getResults()} contains 
	 * exactly the entries corresponding to the types of dice.
	 * @param dice
	 * @param sides
	 */
	@ParameterizedTest(name = "Results of {0}d{1}")
	@MethodSource("diceSetsSupplier")
	void getResults_SizeOfMap(int dice, int sides) {
		Roll roll = new Roll(dice, sides);
		roll.roll();
		Set<Integer> entries =  new HashSet<>();
		entries.add(sides);
		assertEquals(entries, roll.getResults().keySet());
	}
}
