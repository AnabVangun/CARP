package model.values;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ValuesTest {
	/**
	 * Checks that the results returned by {@link Value#getValue()} are consistent.
	 */
	@ParameterizedTest(name = "Test value {0}")
	@ValueSource(ints = {-5, 0, 13})
	public void getValue(int value) {
		assertEquals(value, new Value(value).getValue());
	}
	
	/**
	 * Checks that {@link Value#setValue()} modifies the value.
	 */
	@ParameterizedTest(name = "Test setValue({0})")
	@ValueSource(ints = {-5, 0, 13})
	public void setValue(int newValue) {
		Value v = new Value(0);
		v.setValue(newValue);
		assertEquals(newValue,
					v.getValue());
	}
}
