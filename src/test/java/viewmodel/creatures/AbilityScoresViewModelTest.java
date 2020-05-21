package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import service.parameters.CreatureParameters.AbilityName;

public class AbilityScoresViewModelTest {
	private AbilityScores abilities;
	private AbilityScoresViewModel viewModel;
	/** ViewModel used to verify that a null input behaves as expected */
	private AbilityScoresViewModel nullVM;

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
		viewModel = new AbilityScoresViewModel(abilities);
		nullVM = new AbilityScoresViewModel(null);
	}

	/**
	 * Performs a series of checks defined by
	 * {@link AbilityScoresViewModelTest#testGetAbilityListHelper(AbilityScoresViewModel)}
	 * for a regular viewModel and the null one.
	 */
	@Test
	public void testGetAbilityList() {
		testGetAbilityListHelper(abilities, viewModel);
		testGetAbilityListHelper(null, nullVM);
	}
	
	/**
	 * Checks that the input viewModel returns an ability list with all 
	 * abilities, even if some actually have null values. The ability names 
	 * must be in the proper order, the ability score must be the one given 
	 * as the constructor input.
	 * @param abilities may be null to test a viewModel initialised with null
	 * @param viewModel
	 */
	private void testGetAbilityListHelper(AbilityScores abilities, AbilityScoresViewModel viewModel) {
		AbilityName[] names = AbilityName.values();
		ObservableList<AbilityScoreListItemViewModel> list = viewModel.getAbilityList();
		assertEquals("The observable list must contain as many items as ability names",
				names.length, list.size());
		for (int i = 0; i < names.length; i++) {
			assertEquals("The observable list contains all ability names in the right order",
					names[i].toString(), list.get(i).getAbilityName().get());
			/*
			 * Check ability score. Ignore ability modifier, the
			 * viewmodel is trusted to be self-consistent.
			 */
			if(abilities == null || abilities.getScore(names[i]) == null) {
				assertFalse("If the ability is null, the viewModel contains null",
						list.get(i).isScoreNotNull().get());
			} else {
				assertEquals("If the ability is not null, the viewModel contains the right value",
						abilities.getScore(names[i]).getValue(),
						Integer.parseInt(list.get(i).getAbilityScore().get()));
			}
		}
	}

}
