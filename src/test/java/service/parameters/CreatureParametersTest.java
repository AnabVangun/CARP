package service.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

public class CreatureParametersTest {
	/**
	 * Checks that the parameters for methods defining one have a reasonable 
	 * value.
	 */
	@Test
	public void testAbilityGenerationMethodParams() {
		for(CreatureParameters.AbilityGenerationMethod method : CreatureParameters.AbilityGenerationMethod.values()) {
			switch(method) {
			case DICE_POOL:
			case STANDARD:
				assertTrue("The parameter of " + method.toString() + " must be at least 0", 
						method.getParameter() >= 0);
				break;
			case DIRECT_ASSIGNMENT:
				break;
			default:
				fail("this case should not happen");
			}
		}
	}

}
