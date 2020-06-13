package viewmodel.creatures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import model.creatures.Creature;
import model.creatures.CreatureParameters;

public class CreatureEditionViewModelTest {
	private CreatureEditionViewModel vm;
	private SimpleIntegerProperty index;

	@Before
	public void setUp() throws Exception {
		index = new SimpleIntegerProperty(1);
		vm = new CreatureEditionViewModel(index);
	}
	/**
	 * Checks that the edition bar is not null and has the right
	 * index.
	 */
	@Test
	public void testCreatureEditionViewModel() {
		EditionBarViewModelTest.testGetStatusHelper(1, vm.phasesVM);
	}

	/**
	 * Checks that
	 * {@link CreatureEditionViewModel#getCurrentPhaseDescriptionKey()} returns
	 * a String that corresponds to the name of the relevant value in
	 * {@link Creature.InitStatus}.
	 */
	@Test
	public void testGetCurrentPhaseDescriptionKey() {
		for(int i = 0; i < Creature.InitStatus.values().length; i++) {
			index.set(i);
			assertEquals("The current phase description must be the name of the relevant init status with suffix _PHASE",
					Creature.InitStatus.values()[i].name()+"_PHASE",
					vm.getCurrentPhaseDescriptionKey().getValue());
		}
	}
	
	/**
	 * Checks that when
	 * {@link CreatureEditionViewModel#hasSeveralMethods()} evaluates to true,
	 * {@link CreatureEditionViewModel#getMethodDescriptionKeys()} contains 
	 * several keys, and that each key is associated with a non-null String 
	 * that corresponds to the name of the relevant value in 
	 * {@link Creature.InitStatus}.
	 * 
	 */
	@Test
	public void testHasSeveralMethod() {
		for(int i = 0; i < Creature.InitStatus.values().length; i++) {
			index.set(i);
			assertEquals("There must be as many names as there are descriptions for the methods",
					vm.getMethodNameKeys().size(), 
					vm.getMethodDescriptionKeys().size());
			if(vm.hasSeveralMethods().get()) {
				assertTrue("If the phase has several methods, the method descriptions must have several keys",
						vm.getMethodDescriptionKeys().size() > 1);
				for(int j = 0; j < vm.getMethodNameKeys().size(); j++) {
					String baseName = "";
					switch(Creature.InitStatus.values()[i]) {
					case ABILITIES:
						baseName = CreatureParameters.AbilityGenerationMethod.values()[j].name();
						break;
					default:
						fail("Step " + Creature.InitStatus.values()[i] + " is not supposed to have several methods");
					}
					assertEquals("The method name must be in the relevant Enum with suffix _BUTTON",
							baseName + "_BUTTON", 
							vm.getMethodNameKeys().get(j));
					assertEquals("The method description must be in the relevant Enum with suffix _DESCRIPTION",
							baseName + "_DESCRIPTION",
							vm.getMethodDescriptionKeys().get(j));
				}
			} else {
				assertTrue("If the phase does not have several methods, the method descriptions must be empty",
						vm.getMethodDescriptionKeys().isEmpty());
			}
		}
	}

}
