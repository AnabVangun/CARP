package tools;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;

public interface TestFrameWork<T, S extends TestArguments<T>> {
	/**
	 * @return valid arguments to initialise an object to test.
	 */
	Stream<S> argumentsSupplier();
	
	String testName(String methodName, S args);
	
	/**
	 * Forges a {@link DynamicTest} to run the input test for each element 
	 * returned by the implementation of 
	 * {@link TestFrameWork#argumentsSupplier()}.
	 * @param methodName	to set as the test name.
	 * @param tester		to run as the test.
	 * @return	a stream of nodes running the test.
	 */
	default Stream<DynamicTest> test(String methodName, Consumer<S> tester){
		return test(argumentsSupplier(), methodName, tester);
	}

	/**
	 * Forges a {@link DynamicTest} to run the input test for each element 
	 * of a {@link Stream} of arguments.
	 * @param stream		of arguments, the tests will be run on each 
	 * element.
	 * @param methodName	to set as the test name.
	 * @param tester		to run as the test.
	 * @return	a stream of nodes running the test.
	 */
	default Stream<DynamicTest> test(Stream<S> stream, String methodName, Consumer<S> tester){
		return stream.map(args
				-> dynamicTest(testName(methodName, args), () -> tester.accept(args)));
	}
}
