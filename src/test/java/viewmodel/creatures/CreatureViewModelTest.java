package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import model.creatures.AbilityScoresTest;
import model.creatures.Creature;
import model.creatures.CreatureParameters.AbilityName;
import model.creatures.CreatureTest;

public class CreatureViewModelTest {
	Creature creature;
	CreatureViewModel viewModel;

	@Before
	public void setUp() throws Exception {
		creature = CreatureTest.basicCreature();
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
		ObservableList<AbilityScoreListItemViewModel> actualAbilities = viewModel.getAbilities().get().getListItems();
		ObservableList<AbilityScoreListItemViewModel> expectedAbilities = 
				new AbilityScoresViewModel(creature.getAbilityScores(), creature.getTempAbilityScores())
				.getListItems();
		assertEquals("The lists must be equal, so their length must match",
				expectedAbilities.size(),
				actualAbilities.size());
		for(int i = 0; i < expectedAbilities.size(); i++) {
				assertEquals("The scores must be equal",
						expectedAbilities.get(i).getAbilityScore().get(),
						actualAbilities.get(i).getAbilityScore().get());
		}
	}

	/**
	 * Checks that {@link CreatureViewModel#isInEditMode()} returns true if the
	 * creature is in edit mode and false otherwise.
	 */
	@Test
	public void testIsInEditMode() {
		creature = new Creature();
		viewModel = new CreatureViewModel(creature);
		assertTrue("A not started creature is in edit mode", 
				viewModel.isInEditMode().get());
		testIsInEditModeHelper(Creature.InitStatus.ABILITIES);
		EnumMap<AbilityName, Integer> abilities = AbilityScoresTest.basicAbilityScores();
		for(AbilityName ability : abilities.keySet()) {
			creature.setAbilityScore(ability, abilities.get(ability));
		}
		viewModel.refresh();
		assertTrue("A creature with just ability scores not validated is in edit mode",
				viewModel.isInEditMode().get());
		testIsInEditModeHelper(Creature.InitStatus.ABILITIES);
		if(!creature.validateInitStep()) {
			fail("Something wrong happened, the basic ability scores should be valid");
		}
		viewModel.refresh();
		testIsInEditModeHelper(Creature.InitStatus.REVIEW);
		creature.commit();
		viewModel.refresh();
		assertFalse("A committed creature is not in edit mode",
				viewModel.isInEditMode().get());
		testIsInEditModeHelper(Creature.InitStatus.COMPLETED);
	}
	
	private void testIsInEditModeHelper(Creature.InitStatus expectedStatus) {
		/*
		 * Creature.InitStatus.COMPLETED is not in the list but its return 
		 * index is specified to be the length of the list.
		 */
		int expectedIndex = -6;
		if(expectedStatus == Creature.InitStatus.COMPLETED) {
			expectedIndex = Creature.EDITION_STATUSES.size();
		} else {
			int i = 0;
			boolean search = true;
			for (Creature.InitStatus status : Creature.EDITION_STATUSES) {
				if(status == expectedStatus) {
					expectedIndex = i;
					search = false;
					break;
				}
				i++;
			}
			if (search) {
				fail("The status is neither an edition status nor COMPLETED.");
			}
		}
		assertEquals("The index returned by getCurrentEditionPhase is consistent with the current status",
				expectedIndex,
				viewModel.getCurrentEditionPhase().get());
	}
}
