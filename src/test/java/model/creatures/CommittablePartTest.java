package model.creatures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public interface CommittablePartTest<E, T extends CommittablePart<E>> {
	/**
	 * Container for the input of the tests related to the 
	 * {@link CommittablePart#commit(Object)} method.
	 *
	 * @param <E>	parameter of the {@link CommittablePart} interface.
	 * @param <T>	class implementing the {@link CommittablePart} interface.
	 */
	static class CommitTestInput<E, T extends CommittablePart<E>>{
		final String description;
		final T testObject;
		final E commitArg;
		
		public CommitTestInput(String description, T testObject, E commitArg) {
			this.description = description;
			this.testObject = testObject;
			this.commitArg = commitArg;
		}
	}
	/**
	 * Generates a map of valid objects used by {@link CommittablePartTest}
	 * to test {@link CommittablePart#prepareCommit()}, 
	 * indexed by a String describing their content.
	 * @return
	 */
	Map<String, T> validObjectFactory();
	/**
	 * Generates a map of invalid objects used by {@link CommittablePartTest}
	 * to test {@link CommittablePart#prepareCommit()},  
	 * indexed by a String describing their content.
	 * @return
	 */
	Map<String, T> invalidObjectFactory();
	/**
	 * Generates a collection of valid {@link CommitTestInput} objects used by
	 * {@link CommittablePartTest} to test 
	 * {@link CommittablePart#commit(Object)}.
	 * 
	 * Implementing classes should return at least a case where an object as 
	 * big as possible replaces one as small as possible (in terms of number of
	 * attributes defined), and where an object as small as possible replaces
	 * one as big as possible.
	 * The objects in the {@link CommitTestInput} entries SHOULD be 
	 * independent from each other.
	 * @return
	 */
	Collection<CommitTestInput<E, T>> validCommitParameterFactory();
	
	/**
	 * Modifies the input in some way that invalidates a previous call to
	 * {@link CommittablePart#prepareCommit()}.
	 * @param testObject	object to modify.
	 */
	void modifyTestObject(T testObject);

	/**
	 * Checks that {@link CommittablePart#prepareCommit()} checks that the 
	 * values in the object can be committed and return null if it is valid.
	 * @param testCase		name of the test case
	 * @param testObject	object to test
	 */
	@TestFactory
	default Stream<DynamicTest> prepareCommit_returnNull() {
		return validObjectFactory().entrySet().stream().map(entry 
				-> dynamicTest(entry.getKey(), 
						/*
						 * The method returns null, it is only expected that it
						 *  does not throw an exception.
						 */
						() -> entry.getValue().prepareCommit()
						));
	}
	/**
	 * Checks that {@link CommittablePart#prepareCommit()} checks that the 
	 * values in the object can be committed and throw an 
	 * {@link IllegalStateException} if it is not.
	 * @param testCase		name of the test case
	 * @param testObject	object to test
	 */
	@TestFactory
	default Stream<DynamicTest> prepareCommit_throwException(){
		return invalidObjectFactory().entrySet().stream().map(entry 
				-> dynamicTest(entry.getKey(),
						() -> assertThrows(IllegalStateException.class, 
								() -> entry.getValue().prepareCommit())
						));
	}
	/**
	 * Checks that {@link CommittablePart#commit(Object)} throws an 
	 * {@link IllegalStateException} if {@link CommittablePart#prepareCommit()}
	 * has not been called.
	 * {@link CommittablePart#commit(Object)}.
	 */
	@TestFactory
	default Stream<DynamicTest> commit_failIfUnprepared() {
		return validCommitParameterFactory().stream().map(input
				-> dynamicTest(input.description,
						() -> assertThrows(IllegalStateException.class,
								() -> input.testObject.commit(input.commitArg))));
	}
	/**
	 * Checks that {@link CommittablePart#commit(Object)} throws an 
	 * {@link IllegalStateException} if {@link CommittablePart#prepareCommit()}
	 * has been called but the object has been modified since.
	 */
	@TestFactory
	default Stream<DynamicTest> commit_failIfPreparedWithModification(){
		return validCommitParameterFactory().stream().map(input
				-> dynamicTest(input.description,
						() -> {
							input.testObject.prepareCommit();
							modifyTestObject(input.testObject);
							assertThrows(IllegalStateException.class,
									() -> input.testObject.commit(input.commitArg));
						}));
	}
	/**
	 * Checks that {@link CommittablePart#commit(Object)} modifies its argument
	 * to make it similar to the object on which it was called if it succeeds.
	 */
	@TestFactory
	default Stream<DynamicTest> commit_ModifyValues(){
		return validCommitParameterFactory().stream().map(input
				-> dynamicTest(input.description,
						() -> {
							input.testObject.prepareCommit();
							input.testObject.commit(input.commitArg);
							assertEquals(input.testObject, input.commitArg);
						}));
	}
}
