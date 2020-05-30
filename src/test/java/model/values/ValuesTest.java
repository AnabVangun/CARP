package model.values;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ValuesTest {
	Value v;

	@Before
	public void setUp() throws Exception {
		v = new Value(0);
	}
	/**
	 * Checks that the results returned by {@link Value#getValue()} are consistent.
	 */
	@Test
	public void testGetValue() {
		//XXX When bonuses exist, take them into account.
		assertEquals("A newly generated value must keep its argument as its total value", 0, v.getValue());
		assertEquals("Negative values work", -5, new Value(-5).getValue());
		assertEquals("Positive values work", 13, new Value(13).getValue());
	}
}
