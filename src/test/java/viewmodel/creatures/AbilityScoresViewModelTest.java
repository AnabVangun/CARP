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
	}

	@Test
	public void testGetAbilityList() {
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
			if(abilities.getScore(names[i]) == null) {
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
