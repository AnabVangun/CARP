package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.value.ObservableBooleanValue;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;

public class AbilityScoreListItemViewModelTest {
	private AbilityScores abilities;
	private Map<AbilityName, AbilityScoreListItemViewModel> viewModels;
	/** Specific view model for null ability scores */
	private AbilityScoreListItemViewModel nullViewModel;
	private AbilityName nullAbilityName;

	/**
	 * This tests the constructor for different types of valid Ability scores, 
	 * as well as a null input.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		abilities = AbilityScores.create(AbilityScoresTest.basicAbilityScores());
		viewModels = new EnumMap<AbilityName, AbilityScoreListItemViewModel>(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry: abilities) {
			viewModels.put(entry.getKey(), new AbilityScoreListItemViewModel(entry.getKey(), entry.getValue()));
		}
		nullAbilityName = AbilityName.STRENGTH;
		nullViewModel = new AbilityScoreListItemViewModel(nullAbilityName, null);
	}

	/**
	 * Verifies that the constructor throws an exception when the name of the
	 * ability is null.
	 */
	@Test
	public void testNullInput() {
		try {
			new AbilityScoreListItemViewModel(null, null);
			fail("An AbilityScoreListItemViewModel cannot accept a null ability name");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(null, abilities.getScore(AbilityName.STRENGTH));
			fail("An AbilityScoreListItemViewModel cannot accept a null ability name with a non-null ability");
		} catch (NullPointerException e) {}
	}
	
	/**
	 * Verifies that the ability name is correctly returned
	 */
	@Test
	public void testGetAbilityName() {
		assertEquals("The null viewModel must correctly return its ability name",
				nullAbilityName.toString(), nullViewModel.getAbilityName());
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			assertEquals("A non-null viewModel must correctly return its ability name",
					entry.getKey().toString(), entry.getValue().getAbilityName());
		}
	}

	/**
	 * Verifies that the ability score is correctly returned or that a NPE is 
	 * thrown when the ability score is null.
	 */
	@Test
	public void testGetAbilityScore() {
		try {
		assertEquals(null, nullViewModel.getAbilityScore());
		fail("The null viewModel must throw a null pointer exception");
		} catch (NullPointerException e) {}
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			assertEquals("A non-null viewModel must correctly return its ability score",
					abilities.getScore(entry.getKey()).getValue(), Integer.parseInt(entry.getValue().getAbilityScore().get()));
		}
	}

	/**
	 * Verifies that the ability modifier is correctly returned and formatted:
	 * 1. it can be parsed as an integer corresponding to the modifier
	 * 2. it always includes its sign ('+' for zero)
	 */
	@Test
	public void testGetAbilityModifier() {
		/** String used to verify that the modifier of the null ability is properly formatted */
		String zeroModifier = null;
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			String value = entry.getValue().getAbilityModifier().getValue();
			assertEquals("The ability modifier must be a string representing an integer",
					abilities.getModifier(entry.getKey()), 
					Integer.parseInt(value));
			assertTrue("The ability modifier must include its sign, received " + value 
					+ " for AbilityScore " + entry.getKey(),
					value.startsWith("+")||value.startsWith("-"));
			if(abilities.getModifier(entry.getKey()) == 0) {
				zeroModifier = value;
			}
		}
		if(zeroModifier == null) {
			fail("This test must be rewritten to check that the modifier of the null ability is properly formatted");
		}
		else {
			assertEquals("The modifier of the null ability is 0", 
					zeroModifier,
					nullViewModel.getAbilityModifier().getValue());
		}
	}

	/**
	 * Checks that the 
	 * {@link AbilityScoreListItemViewModel#isScoreModifiable()} method returns
	 * an observable boolean which can be set to true or false but is false by 
	 * default.
	 */
	@Test
	public void testIsScoreModifiable() {
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
					testScoreModifiabilityHelper(entry.getValue());
		}
		testScoreModifiabilityHelper(nullViewModel);
	}
	
	private void testScoreModifiabilityHelper(AbilityScoreListItemViewModel viewModel) {
		//Check just this once that observable values behave as expected
		ObservableBooleanValue value = viewModel.isScoreModifiable();
		assertFalse("By default, a score is not modifiable",
				viewModel.isScoreModifiable().get());
		viewModel.setIsScoreModifiable(true);
		assertTrue("After setting it to true, the value is true",
				viewModel.isScoreModifiable().get());
		assertTrue("The Observable should be updated to true", value.get());
		viewModel.setIsScoreModifiable(false);
		assertFalse("After setting it to false, the value is false",
				viewModel.isScoreModifiable().get());
		assertFalse("The Observable should be updated to false", value.get());
		viewModel.setIsScoreModifiable(false);
		assertFalse("Setting to false a false variable should not be an issue",
				viewModel.isScoreModifiable().get());
	}
	
	/**
	 * Checks that the 
	 * {@link AbilityScoreListItemViewModel#isScoreNotNull()} method returns
	 * an observable boolean which is false if the ability score is null.
	 */
	@Test
	public void testIsScoreNotNull() {
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			assertTrue("A non-null ability must have a true isScoreNotNull property",
					entry.getValue().isScoreNotNull().get());
		}
		assertFalse("A null ability must have a false isScoreNotNull property",
				nullViewModel.isScoreNotNull().get());
	}

}
