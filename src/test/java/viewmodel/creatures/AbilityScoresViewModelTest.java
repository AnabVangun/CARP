package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.Creature;
import model.creatures.CreatureParameters.AbilityGenerationMethod;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import viewmodel.tools.ViewModelParameters;

public class AbilityScoresViewModelTest {
	private AbilityScores abilities;
	private AbilityScoresViewModel viewModel;
	/** ViewModel used to verify that a null input behaves as expected */
	private AbilityScoresViewModel nullVM;
	/** 
	 * ViewModel used to verify that abilities and temporary abilities are 
	 * properly set. 
	 * */
	private AbilityScoresViewModel nullifiedVM;
	private ReadOnlyBooleanWrapper isCreatureEditable = new ReadOnlyBooleanWrapper(true);

	@Before
	public void setUp() throws Exception {
		//Remove one non-essential ability
		EnumMap<AbilityName, Integer> map = AbilityScoresTest.basicAbilityScores();
		for(AbilityName name : AbilityName.values()) {
			if(!AbilityScores.MANDATORY_ABILITIES.contains(name)) {
				map.remove(name);
				break;
			}
		}
		abilities = AbilityScores.create(map);
		Creature creature = new Creature();
		for(Map.Entry<AbilityName, AbilityScore> entry: abilities) {
			creature.setAbilityScore(entry.getKey(), 
					entry.getValue().isDefined() ? entry.getValue().getValue() : null);
		}
		creature.commit();
		creature.edit();
		for(Map.Entry<AbilityName, Integer> entry: AbilityScoresTest.basicAbilityScores().entrySet()) {
			creature.setAbilityScore(entry.getKey(), entry.getValue());
		}
		viewModel = new AbilityScoresViewModel(creature, isCreatureEditable.getReadOnlyProperty());
		nullVM = new AbilityScoresViewModel(new Creature(), isCreatureEditable);
		creature.commit();
		creature.edit();
		for(AbilityName name : AbilityName.values()) {
			creature.setAbilityScore(name, null);
		}
		nullifiedVM = new AbilityScoresViewModel(creature, isCreatureEditable.getReadOnlyProperty());
	}

	/**
	 * Performs a series of checks defined by
	 * {@link AbilityScoresViewModelTest#testGetAbilityListHelper(AbilityScoresViewModel)}
	 * for a regular viewModel and the null one.
	 */
	@Test
	public void testGetAbilityList() {
		for(boolean creatureEditability : new boolean[] {true, false}) {
			//Check with creature editable and not, it should not matter.
			isCreatureEditable.set(creatureEditability);
			testGetAbilityListHelper(AbilityScores.create(AbilityScoresTest.basicAbilityScores()), viewModel);
			testGetAbilityListHelper(AbilityScores.create(null), nullVM);
			testGetAbilityListHelper(AbilityScores.create(null), nullifiedVM);
			for(AbilityScoreListItemViewModel vm : nullifiedVM.getListItems()) {
				assertEquals("All scores should be nullified when the tmp abilities are null and the abilities not",
						ViewModelParameters.Styles.NULLIFY.name(), 
						vm.getScoreStyleClasses().get(0));
			}
		}
	}
	
	/**
	 * Checks that the input viewModel returns an ability list with all 
	 * abilities, even if some actually have non-defined values. The ability 
	 * names 
	 * must be in the proper order, the ability score must be the one given 
	 * as the constructor input.
	 * @param tmpAbilities
	 * @param viewModel
	 */
	private void testGetAbilityListHelper(AbilityScores tmpAbilities, AbilityScoresViewModel viewModel) {
		for(boolean creatureEditability : new boolean[] {true, false}) {
			//Check with creature editable and not, it should not matter.
			isCreatureEditable.set(creatureEditability);
			AbilityName[] names = AbilityName.values();
			ObservableList<AbilityScoreListItemViewModel> list = viewModel.getListItems();
			assertEquals("The observable list must contain as many items as ability names",
					names.length, list.size());
			for (int i = 0; i < names.length; i++) {
				assertEquals("The observable list contains all ability names in the right order",
						names[i].toString(), list.get(i).getAbilityName());
				/*
				 * Check ability score. Ignore ability modifier, the
				 * viewmodel is trusted to be self-consistent.
				 */
				if(! tmpAbilities.getScore(names[i]).isDefined()) {
					assertEquals("If the ability is not defined, the viewModel contains null",
							"",
							list.get(i).getAbilityScore().get());
				} else {
					assertEquals("If the ability is not null, the viewModel contains the right value",
							tmpAbilities.getScore(names[i]).getValue(),
							Integer.parseInt(list.get(i).getAbilityScore().get()));
				}
			}
		}
	}
	
	/**
	 * Checks that setGenerationMethod causes the expected modifications in 
	 * the {@link AbilityScoreListItemViewModel} objects.
	 * 1. For {@link AbilityGenerationMethod#DIRECT_ASSIGNMENT}, all list items
	 * become editable.
	 */
	@Test
	public void testSetGenerationMethod() {
		isCreatureEditable.set(true);
		for(AbilityScoresViewModel vm : new AbilityScoresViewModel[] {viewModel, nullVM}) {
			vm.setGenerationMethod(AbilityGenerationMethod.DICE_POOL); //TODO test expectation
			vm.setGenerationMethod(AbilityGenerationMethod.DIRECT_ASSIGNMENT);
			testSetGenerationMethodHelper(vm, AbilityGenerationMethod.DIRECT_ASSIGNMENT);
			vm.setGenerationMethod(AbilityGenerationMethod.STANDARD); //TODO test expectation
		}
		isCreatureEditable.set(false);
		for(AbilityScoresViewModel vm : new AbilityScoresViewModel[] {viewModel, nullVM}) {
			for(AbilityGenerationMethod method : AbilityGenerationMethod.values()) {
				try{
					vm.setGenerationMethod(method);
					fail("It should not be possible to set generation method when creature is not editable, "
							+ "it was for " + method.name());
				} catch (IllegalStateException e) {};
			}
		}
	}
	
	/**
	 * Checks that the input viewmodel has correctly executed the tasks 
	 * expected after setting the generation method.
	 * @param vm		viewmodel that is supposed to have acted.
	 * @param method	generation method that was passed to the view model.
	 */
	static protected void testSetGenerationMethodHelper(AbilityScoresViewModel vm, 
			AbilityGenerationMethod method) {
		switch(method) {
		case DICE_POOL:
			fail("Test not yet implemented");
			break;
		case DIRECT_ASSIGNMENT:
			for(AbilityScoreListItemViewModel listVM : vm.getListItems()) {
				assertTrue("Using DirectAssignment, all list items must become editable",
						listVM.isScoreEditable().get());
			}
			break;
		case STANDARD:
			fail("Test not yet implemented");
			break;
		default:
			fail("Test not yet implemented");
		}
	}

}
