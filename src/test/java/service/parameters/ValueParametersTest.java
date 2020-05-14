package service.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValueParametersTest {

	/**
	 * Checks that the ability score parameters have reasonable values.
	 */
	@Test
	public void testAbilityScoreParameters() {
		assertEquals("MIN_ABILITY_SCORE must be 0.", 0, ValueParameters.MIN_ABILITY_SCORE);
		assertTrue("MAX_ABILITY_SCORE must be at least 40.", ValueParameters.MAX_ABILITY_SCORE > 40);
	}
	
	/**
	 * Checks that the roll parameters have reasonable values.
	 */
	@Test
	public void testRollParameters() {
		assertEquals("MIN_NUMBER_OF_DICE must be 1.", 1, ValueParameters.MIN_NUMBER_OF_DICE);
		assertTrue("MIN_NUMBER_OF_SIDES must be at least 1.", ValueParameters.MIN_NUMBER_OF_SIDES >= 1);
		assertTrue("MIN_NUMBER_OF_SIDES must be at most 4.", ValueParameters.MIN_NUMBER_OF_SIDES <= 4);
		assertTrue("MAX_NUMBER_OF_DICE must be at least 10.", ValueParameters.MAX_NUMBER_OF_DICE >= 10);
		assertTrue("MAX_NUMBER_OF_SIDES must be at least 100.", ValueParameters.MAX_NUMBER_OF_SIDES >= 100);
	}
}
