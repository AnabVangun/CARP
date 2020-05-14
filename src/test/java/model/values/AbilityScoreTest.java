package model.values;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbilityScoreTest {

	@Test
	public void testComputeModifier() {
		int[] modifiers = new int[] {-5, -5, -4, -4, -3, -3, -2, -2, -1, -1, 0,
				0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
		for(int i = 0; i < modifiers.length; i++) {
			assertEquals("Modifiers must be conform to the rulebook",
					modifiers[i], AbilityScore.computeModifier(i));
		}
	}
}
