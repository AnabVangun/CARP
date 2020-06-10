package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.CreatureParameters.AbilityName;
import viewmodel.creatures.CreatureViewModelParameters.Modification;

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
		viewModel = new AbilityScoresViewModel(abilities, 
				AbilityScores.create(AbilityScoresTest.basicAbilityScores()));
		nullVM = new AbilityScoresViewModel(AbilityScores.create(null), AbilityScores.create(null));
		nullifiedVM = new AbilityScoresViewModel(AbilityScores.create(AbilityScoresTest.basicAbilityScores()),
				AbilityScores.create(null));
	}

	/**
	 * Performs a series of checks defined by
	 * {@link AbilityScoresViewModelTest#testGetAbilityListHelper(AbilityScoresViewModel)}
	 * for a regular viewModel and the null one.
	 */
	@Test
	public void testGetAbilityList() {
		testGetAbilityListHelper(AbilityScores.create(AbilityScoresTest.basicAbilityScores()), viewModel);
		testGetAbilityListHelper(AbilityScores.create(null), nullVM);
		testGetAbilityListHelper(AbilityScores.create(null), nullifiedVM);
		for(AbilityScoreListItemViewModel vm : nullifiedVM.getListItems()) {
			assertEquals("All scores should be nullified when the tmp abilities are null and the abilities not",
					Modification.NULLIFY.name(), 
					vm.getScoreStyleClasses().get(0));
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
