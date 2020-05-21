package viewmodel.creatures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.Creature;

public class CreatureViewModelTest {
	Creature creature;
	CreatureViewModel viewModel;

	@Before
	public void setUp() throws Exception {
		creature = new Creature();
		creature.setAbilityScores(AbilityScores.create(AbilityScoresTest.basicAbilityScores()));
		viewModel = new CreatureViewModel(creature);
	}

	/**
	 * Checks that the constructor throws a null pointer exception on a null input
	 */
	@Test
	public void testCreatureViewModel() {
		try {
			viewModel = new CreatureViewModel(null);
			fail("A null input should throw a NullPointerException");
		} catch (NullPointerException e) {}
	}

	/**
	 * Checks that the viewModel returned by getAbilities is consistent with the 
	 * creature's abilities.
	 */
	@Test
	public void testGetAbilities() {
		ObservableList<AbilityScoreListItemViewModel> actualAbilities = viewModel.getAbilities().get().getAbilityList();
		ObservableList<AbilityScoreListItemViewModel> expectedAbilities = 
				new AbilityScoresViewModel(creature.getAbilityScores())
				.getAbilityList();
		assertEquals("The lists must be equal, so their length must match",
				expectedAbilities.size(),
				actualAbilities.size());
		for(int i = 0; i < expectedAbilities.size(); i++) {
			assertEquals("The scores must be equal: if one is null, so is the other",
					expectedAbilities.get(i).isScoreNotNull().get(), 
					actualAbilities.get(i).isScoreNotNull().get());
			if(expectedAbilities.get(i).isScoreNotNull().get()) {
				assertEquals("The scores must be equal",
						expectedAbilities.get(i).getAbilityScore().get(),
						actualAbilities.get(i).getAbilityScore().get());
			}
		}
	}

}
