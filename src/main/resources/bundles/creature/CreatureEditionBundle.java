package bundles.creature;

import java.util.ListResourceBundle;

import model.creatures.Creature;

public class CreatureEditionBundle extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return contents;
	}
	
	private Object[][] contents = {
			{Creature.InitStatus.ABILITIES.name()+"_PHASE", "Each character has six ability scores that represent his character’s most basic attributes. They are his raw talent and prowess. "
					+ "While a character rarely rolls an ability check (using just an ability score), these scores, and the modifiers they create, affect nearly every aspect of a character’s skills and abilities. "
					+ "Each ability score generally ranges from 3 to 18, although racial bonuses and penalties can alter this; an average ability score is 10. There are a number of different methods used to generate ability scores. "
					+ "Each of these methods gives a different level of flexibility and randomness to character generation.\r\n\r\n" + 
					"Racial modifiers (adjustments made to your ability scores due to your character’s race—see Chapter 2) are applied after the scores are generated."}
	};

}
