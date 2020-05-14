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

	@Test
	public void testGetValue() {
		assertEquals("A newly generated value must keep its argument as its total value", 0, v.getValue());
		assertEquals("Negative values work", -5, new Value(-5).getValue());
		assertEquals("Positive values work", 13, new Value(13).getValue());
	}

//	@Test
//	public void testAddBonus() {
//		v.addBonus(1, Type.RACIAL, true, true);
//		assertEquals("Adding a simple bonus to a bonusless value must work",
//				1, v.getValue());
//		fail("Not yet implemented"); // TODO
//	}
//
//	@Test
//	public void testRemoveBonus() {
//		fail("Not yet implemented"); // TODO
//	}

}
