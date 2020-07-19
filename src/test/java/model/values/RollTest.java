package model.values;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import tools.TestArguments;
import tools.TestFrameWork;

public class RollTest implements TestFrameWork<Roll, RollArguments>{
	/*
	 * XXX This class only tests with one type of dice.
	 * When Roll handle several types of dice at the same time, 
	 * this method will have to be adapted.
	 * XXX This class does not take bonuses into account. When Roll handle
	 * them, this method will have to be adapted.
	 */
	public static final long SEED = (long) 2;
	private static final int REPEAT = 25;
	//Short hands for the parameter related to rolls
	private static final int MAX_DICE = ValueParameters.MAX_NUMBER_OF_DICE;
	private static final int MAX_SIDES = ValueParameters.MAX_NUMBER_OF_SIDES;
	private static final int MIN_DICE = ValueParameters.MIN_NUMBER_OF_DICE;
	private static final int MIN_SIDES = ValueParameters.MIN_NUMBER_OF_SIDES;
	@Override
	public Stream<RollArguments> argumentsSupplier () {
		return Stream.of(new RollArguments(2, 6), 
			new RollArguments(MAX_DICE, MAX_SIDES),
			new RollArguments(MIN_DICE, MIN_SIDES));
	}
	@Override
	public String testName(String methodName, RollArguments roll) {
		return String.format("%s.%s on %s", Roll.class.getSimpleName(), methodName, roll);
	}
	
	@BeforeEach
	void setUp() {
		Roll.seed(SEED);
	}

	/**
	 * Checks that {@link Roll#Roll(int, int)} builds a {@link Roll} object
	 * without raising an exception on valid input.
	 */
	@TestFactory
	Stream<DynamicTest> rollConstructor_ValidValues() {
		return test("Roll(int, int) (no exception)", 
				roll -> assertDoesNotThrow(() -> roll.convert()));
	}
	/**
	 * Checks that {@link Roll#Roll(int, int)} throws an 
	 * {@link IllegalArgumentException} exception on invalid input.
	 */
	@TestFactory
	Stream<DynamicTest> rollConstructor_failOnInvalidValues() {
		Stream<RollArguments> args = Stream.of(
				new RollArguments(MIN_DICE - 4, 6), new RollArguments(MIN_DICE - 1, 13),
				new RollArguments(1, MIN_SIDES - 3), new RollArguments(5, MIN_SIDES - 1),
				new RollArguments(MIN_DICE - 1, MIN_SIDES - 1), new RollArguments(MAX_DICE + 1, 6),
				new RollArguments(5, MAX_SIDES + 1), new RollArguments(MAX_DICE + 25, MAX_SIDES + 3)
						);
		return test(args, "Roll(int, int) (invalid values)", 
				roll -> assertThrows(IllegalArgumentException.class, () -> roll.convert()));
	}
	/**
	 * Checks that {@link Roll#roll()} returns the sum of the values of the
	 * individual dice in the set as returned by {@link Roll#getResults()}.
	 */
	@TestFactory
	Stream<DynamicNode> roll_SumConsistentWithResults() {
		return argumentsSupplier().map(args
				-> {
					String message = testName("roll() (consistent sum)", args);
					return dynamicContainer(message, 
							//Test each set of args REPEAT times
							IntStream.range(0, REPEAT).mapToObj(i
									-> dynamicTest(String.format("%s (iter %d)", message, i), 
											() -> roll_SumConsistentWithResults(args))));
				});
	}
	/**
	 * Checks that {@link Roll#roll()} returns the sum of the values of the
	 * individual dice in the set as returned by {@link Roll#getResults()}.
	 * @param argument	defining the dice to roll.
	 */
	void roll_SumConsistentWithResults(RollArguments arg) {
		Roll roll = arg.convert();
		int directResult = roll.roll();
		int expectedSum = 0;
		for(int i : roll.getResults().get(arg.sides)) {
			expectedSum += i;
		}
		assertEquals(expectedSum, directResult,
				() -> "The result of throwing " + arg
				+ "is the sum of the value of each die");
	}
	/**
	 * Checks that the {@link Roll#getResults()} method returns results 
	 * consistent with the dice.
	 */
	@TestFactory
	Stream<DynamicNode> getResults_ConsistentWithDiceParams(){
		return argumentsSupplier().map(args
				-> {
					String message = testName("getResults() (consistency)", args);
					return dynamicContainer(message, 
							//Test each set of args REPEAT times
							IntStream.range(0, REPEAT).mapToObj(i
									-> {
										String iterMessage = String.format("%s (iter %d)", message, i);
										return dynamicContainer(iterMessage, 
												getResults_ConsistentWithDiceParams(iterMessage, args));
									}));
				});
	}
	/**
	 * Checks that the {@link Roll#getResults()} method returns results 
	 * consistent with the dice: as many entries as there are dice and valid 
	 * bounds.
	 * @param message that will be used as a base to name the tests
	 * @param args used to initialise the {@link Roll} object to be tested
	 */
	Stream<DynamicNode> getResults_ConsistentWithDiceParams(String message, RollArguments args){
		Roll roll = args.convert();
		roll.roll();
		Integer[] results = roll.getResults().get(args.sides);
		return Stream.of(dynamicTest(message + " (number of dice)",
				() -> assertEquals(args.dice, results.length, 
						"Results must contain as many entries as there are dice")),
				dynamicTest(message + " (bounds)",
						() -> assertAll(Arrays.stream(results).map(i 
								-> getResults_ConsistentWithDiceParams(message, args, i)))));
	}

	/**
	 * Checks that the {@link Roll#getResults()} method returns results 
	 * consistent with the dice: at least 1 and at most the number of sides.
	 * @param message that will be used as a base to name the tests
	 * @param args used to initialise the {@link Roll} object to be tested
	 * @param value result of the roll of a single die
	 * @return an {@link Executable} checking the bounds.
	 */
	Executable getResults_ConsistentWithDiceParams(String message, RollArguments args, Integer value){
		return () -> assertAll(
				() -> assertTrue(value <= args.sides, 
				String.format("Rolling %s produced %d", args, value)),
				() -> assertTrue(value > 0,
						String.format("Rolling %s produced %d", args, value)));
	}
	/**
	 * Checks that seeding the {@link Roll} class seeds the global RNG used by
	 * all instances of the class.
	 */
	@TestFactory
	Stream<DynamicNode> seed_seedsRNG() {
		int numberOfSides = 6;
		Roll first = new Roll(1, numberOfSides);
		Roll second = new Roll(1, numberOfSides);
		Roll pair = new Roll(2, numberOfSides);
		Roll.seed(SEED);
		Integer[] resultSeparate = new Integer[] {first.roll(), second.roll()};
		Roll.seed(SEED);
		pair.roll();
		Integer[] resultPair = pair.getResults().get(numberOfSides);
		String message = testName("seed()", new RollArguments(2, numberOfSides)); 
		return Stream.of(dynamicTest(message + " (first die)", 
				() -> assertEquals(resultSeparate[0], resultPair[0])),
				dynamicTest(message + " (second die)", 
						() -> assertEquals(resultSeparate[1], resultPair[1])));
	}
	/**
	 * Checks that {@link Roll#getResults()} does not roll the dice and returns
	 * an array of null Integer for each number of sides in the set.
	 */
	@TestFactory
	Stream<DynamicNode> getResults_DoesNotRollTheDice() {
		return argumentsSupplier().map(args
				-> {
					String message = testName("getResults() (without rolling)", args);
					Roll noRoll = args.convert();
					Map<Integer, Integer[]> noRollResults = noRoll.getResults();
					Map<Integer, Integer[]> nullResults = new HashMap<>();
					nullResults.put(args.sides, new Integer[args.dice]);
					return contentEqualityChecker(message, noRollResults, nullResults, true);
				});
	}
	/**
	 * Checks that different calls to {@link Roll#getResults()} return maps 
	 * that contain the same results even if the dice are rolled in between.
	 * That is, checks that the map returned by the method is updated whenever
	 * the dice are rolled and always contains updated values.
	 */
	@TestFactory
	Stream<DynamicNode> getResults_EqualMaps() {
		return argumentsSupplier().map(args 
				-> {
					Roll roll = args.convert();
					Map<Integer, Integer[]> firstResults = roll.getResults();
					roll.roll();
					Map<Integer, Integer[]> secondResults = roll.getResults();
					return contentEqualityChecker(testName("getResults() (with rolling)", args),
							firstResults, secondResults, true);
				});
	}
	/**
	 * Checks that the map returned by {@link Roll#getResults()} change when 
	 * the dice are rolled, unless they are equal by chance.
	 */
	@TestFactory
	Stream<DynamicNode> getResults_ResultsChangeWhenDiceRolled(){
		return argumentsSupplier().map(args -> getResults_ResultsChangeWhenDiceRolled(args));
	}
	/**
	 * Checks that the map returned by {@link Roll#getResults()} change when 
	 * the dice are rolled, unless they are equal by chance.
	 * @param args	used to initialise a {@link Roll} object.
	 * @return a {@link DynamicNode} asserting that the map changes.
	 */
	private DynamicNode getResults_ResultsChangeWhenDiceRolled(RollArguments args) {
		assumeTrue(args.sides >= 1, 
				"There must be more than one side for this test to make sense");
		Roll roll = args.convert();
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
		return contentEqualityChecker(testName("getResults() (results change after rolling)", args),
				deepCopy, secondResults, false);
	}

	/**
	 * Produces a test node comparing the two input maps as per the equality
	 * param.
	 * @param name		Base name to include in the tests.
	 * @param first		First map to compare
	 * @param second	Second map to compare
	 * @param equality	If true, checks that the two maps have the same 
	 * content. Else, checks that the two maps have the same keys but with
	 * different associated values.
	 * @return			a {@link DynamicNode} asserting that the key sets of 
	 * the two maps are equals, and that the associated values are as expected.
	 */
	private DynamicNode contentEqualityChecker(String name, 
			Map<Integer, Integer[]> first, Map<Integer, Integer[]> second,
			boolean equality) {
		Consumer<Integer> tester = (i) -> {
			if(equality) {
				assertArrayEquals(first.get(i), second.get(i));
			} else {
				assertThrows(AssertionError.class, () -> assertArrayEquals(first.get(i), second.get(i)));
			}
		};
		String equalityString = equality? "equality" : "inequality";
		return dynamicContainer(name, Stream.of(
				dynamicTest(name + " (key set equality)", () -> assertEquals(first.keySet(), second.keySet())),
				dynamicContainer(String.format("%s (value array %s)", name, equalityString), 
						first.keySet().stream().map(key
						-> dynamicTest(String.format("%s (value array %s for key %d)", name, equalityString, key),
								() -> tester.accept(key))))));
	}
	/**
	 * Checks that the map returned by {@link Roll#getResults()} contains 
	 * exactly the entries corresponding to the types of dice.
	 */
	@TestFactory
	Stream<DynamicTest> getResults_SizeOfMap() {
		return test("getResults() (key set)", args
				-> {
					Roll roll = args.convert();
					roll.roll();
					Set<Integer> entries =  new HashSet<>();
					entries.add(args.sides);
					assertEquals(entries, roll.getResults().keySet());
		});
	}
}

class RollArguments implements TestArguments<Roll>{
	final int dice;
	final int sides;
	RollArguments(int dice, int sides){
		this.dice = dice;
		this.sides = sides;
	}
	@Override
	public Roll convert() {
		return new Roll(dice, sides);
	}
	@Override
	public String toString() {
		return String.format("%dd%d", dice, sides);
	}
}