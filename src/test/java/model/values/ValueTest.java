package model.values;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import tools.TestArguments;
import tools.TestFrameWork;

public class ValueTest implements TestFrameWork<Value, ValueArguments>{

	@Override
	public Stream<ValueArguments> argumentsSupplier(){
		return Stream.of(new ValueArguments(-5), new ValueArguments(0), new ValueArguments(13));
	}
	@Override
	public String testName(String methodName, ValueArguments args) {
		return String.format("%s.%s on %s", Value.class.getSimpleName(), methodName, args);
	}
	/**
	 * Checks that the results returned by {@link Value#getValue()} are consistent.
	 */
	@TestFactory
	Stream<DynamicTest> getValue() {
		return test("getValue()", (v) -> assertEquals(v.value, v.convert().getValue()));
	}
	
	/**
	 * Checks that {@link Value#setValue()} modifies the value.
	 */
	@TestFactory
	Stream<DynamicTest> setValue() {
		return test("setValue(int)", (v) -> {
			Value value = new Value(0);
			value.setValue(v.value);
			assertEquals(v.value, value.getValue());
		});
	}
}

class ValueArguments implements TestArguments<Value>{
	final int value;
	ValueArguments(int value){
		this.value = value;
	}
	@Override
	public Value convert() {
		return new Value(value);
	}
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
