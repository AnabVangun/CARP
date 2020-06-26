package viewmodel.creatures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import model.creatures.Creature;
import model.creatures.CreatureParameters;
import model.creatures.CreatureParameters.AbilityGenerationMethod;

public class CreatureEditionViewModelTest {
	//XXX all methods in here will need to be checked for any phase of the creature edition process
	private CreatureEditionViewModel vm;
	private CreatureViewModel creatureVM;
//	private SimpleIntegerProperty index;
	private SimpleIntegerProperty methodIndex;

	@Before
	public void setUp() throws Exception {
//		index = new SimpleIntegerProperty(1);
		creatureVM = new CreatureViewModel(new Creature());
		methodIndex = new SimpleIntegerProperty(0);
		vm = new CreatureEditionViewModel(creatureVM);
		methodIndex.bindBidirectional(vm.selectedMethodIndexProperty());
	}
	/**
	 * Checks that the edition bar is not null and has the right
	 * index.
	 */
	@Test
	public void testCreatureEditionViewModel() {
//		EditionBarViewModelTest.testGetStatusHelper(index.get(), vm.phasesVM);
		EditionBarViewModelTest.testGetStatusHelper(creatureVM.getCurrentEditionPhase().get(), vm.phasesVM);
	}

	/**
	 * Checks that
	 * {@link CreatureEditionViewModel#getCurrentPhaseDescriptionKey()} returns
	 * a String that corresponds to the name of the relevant value in
	 * {@link Creature.InitStatus}.
	 */
	@Test
	public void testGetCurrentPhaseDescriptionKey() {
//		for(int i = 0; i < Creature.InitStatus.values().length; i++) {
//			index.set(i);
			assertEquals("The current phase description must be the name of the relevant init status with suffix _PHASE",
//					Creature.InitStatus.values()[i].name()+"_PHASE",
					Creature.InitStatus.values()[creatureVM.getCurrentEditionPhase().get()].name() + "_PHASE",
					vm.getCurrentPhaseDescriptionKey().getValue());
//		}
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
//		for(int i = 0; i < Creature.InitStatus.values().length; i++) {
//			index.set(i);
			if(vm.hasSeveralMethods().get()) {
				assertTrue("If the phase has several methods, the method names must have several keys",
						vm.getMethodNameKeys().size() > 1);
				for(int j = 0; j < vm.getMethodNameKeys().size(); j++) {
					String baseName = "";
					methodIndex.set(j);
//					switch(Creature.InitStatus.values()[i]) {
					switch(Creature.InitStatus.values()[creatureVM.getCurrentEditionPhase().get()]) {
					case ABILITIES:
						baseName = CreatureParameters.AbilityGenerationMethod.values()[j].name();
						break;
					default:
//						fail("Step " + Creature.InitStatus.values()[i] + " is not supposed to have several methods");
						fail("Step " + Creature.InitStatus.values()[creatureVM.getCurrentEditionPhase().get()] 
								+ " is not supposed to have several methods");
					}
					assertEquals("The method name must be in the relevant Enum with suffix _BUTTON",
							baseName + "_BUTTON", 
							vm.getMethodNameKeys().get(j));
					assertEquals("The method description must be in the relevant Enum with suffix _DESCRIPTION",
							baseName + "_DESCRIPTION",
							vm.getMethodDescriptionKey().getValue());
				}
			} else {
				assertTrue("If the phase does not have several methods, the method names must be empty",
						vm.getMethodNameKeys().isEmpty());
			}
//		}
	}
	
	/**
	 * Checks that {@link CreatureEditionViewModel#isActionExpected()} returns
	 * a properly initialised {@link ObservableBooleanValue} and not null.
	 */
	@Test
	public void testIsChoiceExpected() {
//		for(int i = 0; i < Creature.InitStatus.values().length; i++) {
//			index.set(i);
			assertNotNull("isChoiceExpected unexpectedly returned null in phase " + 
//					Creature.InitStatus.values()[i],
					Creature.InitStatus.values()[creatureVM.getCurrentEditionPhase().get()],
					vm.isActionExpected());
//		}
	}
	
	/**
	 * Checks that changing the selected method correctly updates the relevant
	 * fields.
	 */
	@Test
	public void testMethodRefresh() {
		//Creature is in the AbilityGeneration phase.
		testAbilityGenerationMethodRefresh();
	}
	
	/**
	 * Checks for each of the ability generation methods that the ability 
	 * scores viewModel properly sets itself in accordance with the method.
	 */
	private void testAbilityGenerationMethodRefresh() {
		methodIndex.set(AbilityGenerationMethod.DIRECT_ASSIGNMENT.ordinal());
		//Verify that the ability scores of the creature are editable
		AbilityScoresViewModelTest.testSetGenerationMethodHelper(creatureVM.getAbilities().get(), 
				AbilityGenerationMethod.DIRECT_ASSIGNMENT);
	}
	
	@Test
	public void validateModificationStep_failWithMissingAbilities_PhaseUnchanged() {
		int currentPhase = vm.currentPhaseIndex.get();
		vm.validateModificationStep();
		assertEquals("The current phase must not change when some mandatory abilities are missing",
				currentPhase, vm.currentPhaseIndex.get());
	}
	
	@Test
	public void validateModificationStep_failWithMissingAbilities_StyleUpdated() {
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_failWithInvalidAbilities_PhaseUnchanged() {
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_failWithInvalidAbilities_StyleUpdated() {
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_succeedWithMissingAbilities_PhaseChanged() {
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_succeedWithMissingAbilities_StyleUpdated() {
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_succeedWithValidAbilities_PhaseChanged(){
		fail("not yet implemented");//TODO
	}
	
	@Test
	public void validateModificationStep_succeedWithValidAbilities_StyleUpdated(){
		fail("not yet implemented");//TODO
	}
}
