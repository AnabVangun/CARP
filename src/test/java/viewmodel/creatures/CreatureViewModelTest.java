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
		ObservableList<AbilityScoreListItemViewModel> actualAbilities = viewModel.getAbilities().get().getListItems();
		ObservableList<AbilityScoreListItemViewModel> expectedAbilities = 
				new AbilityScoresViewModel(creature.getAbilityScores())
				.getListItems();
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
		creature.setAbilityScores(AbilityScores.create(AbilityScoresTest.basicAbilityScores()));
		viewModel.refresh();
		assertTrue("A creature with just ability scores is in edit mode",
				viewModel.isInEditMode().get());
		testIsInEditModeHelper(Creature.InitStatus.REVIEW);//FIXME this fails because the viewmodel is not refreshed
		creature.finish();
		viewModel.refresh();
		assertFalse("A completed creature is not in edit mode",
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
