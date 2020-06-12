package viewmodel.creatures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import model.creatures.Creature;

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

}
