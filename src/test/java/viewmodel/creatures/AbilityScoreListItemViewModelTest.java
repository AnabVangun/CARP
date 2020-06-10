package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.Creature;
import model.creatures.CreatureTest;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.ValueParameters;

public class AbilityScoreListItemViewModelTest {
	private AbilityScores abilities;
	private Map<AbilityName, AbilityScoreListItemViewModel> viewModels;
	private AbilityScores tmpAbilities;
	/** Specific view models for null ability scores */
	private AbilityScoreListItemViewModel nullViewModel;
	private AbilityName nullAbilityName;
	private AbilityScore nullAbility;
	private AbilityScore tmpNullAbility;
	private Creature creature;
	

	/**
	 * This tests the constructor for different types of valid Ability scores, 
	 * as well as a null input.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		creature = CreatureTest.basicCreature();
		abilities = creature.getAbilityScores();
		//initialise tmpAbilities with values better, worse and identical to abilities
		EnumMap<AbilityName, Integer> values = AbilityScoresTest.basicAbilityScores();
		int i = -2;
		for(AbilityName ability: AbilityName.values()) {
			values.put(ability, values.get(ability) + i);
			i++;
		}
		tmpAbilities = AbilityScores.create(values);
		viewModels = new EnumMap<AbilityName, AbilityScoreListItemViewModel>(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry: abilities) {
			viewModels.put(entry.getKey(),
					new AbilityScoreListItemViewModel(entry.getKey(), entry.getValue(),
							tmpAbilities.getScore(entry.getKey())));
		}
		AbilityScores nullAbilities = AbilityScores.create(null);
		nullAbilityName = AbilityName.STRENGTH;
		nullAbility = nullAbilities.getScore(nullAbilityName);
		tmpNullAbility = tmpAbilities.getScore(nullAbilityName);
		nullViewModel = new AbilityScoreListItemViewModel(nullAbilityName,
				nullAbility, tmpNullAbility);
	}

	/**
	 * Verifies that the constructor throws an exception when either argument 
	 * is null.
	 */
	@Test
	public void testNullInput() {
		try {
			new AbilityScoreListItemViewModel(nullAbilityName, abilities.getScore(nullAbilityName), null);
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(nullAbilityName, null, abilities.getScore(nullAbilityName));
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(null, abilities.getScore(nullAbilityName), abilities.getScore(nullAbilityName));
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(nullAbilityName, null, null);
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(null, abilities.getScore(nullAbilityName), null);
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(null, null, abilities.getScore(nullAbilityName));
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
		} catch (NullPointerException e) {}
		try {
			new AbilityScoreListItemViewModel(null, null, null);
			fail("An AbilityScoreListItemViewModel cannot accept a null parameter");
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
	 * Verifies that the ability score is correctly returned (empty string for 
	 * the nullViewModel)
	 */
	@Test
	public void testGetAbilityScore() {
		testGetAbilityScoreHelper(nullViewModel, tmpNullAbility);
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			testGetAbilityScoreHelper(entry.getValue(), tmpAbilities.getScore(entry.getKey()));
		}
	}
	
	/**
	 * Verifies that the ability score is correctly returned by the given
	 * view model.
	 * @param vm		to check
	 * @param tmpExpected	ability score formatted by the view model.
	 */
	private void testGetAbilityScoreHelper(AbilityScoreListItemViewModel vm, AbilityScore tmpExpected) {
		assertEquals("A non-null viewModel must correctly return its tmp ability score",
				tmpExpected.isDefined() ? tmpExpected.getValue() : "", 
				tmpExpected.isDefined() ? Integer.parseInt(vm.getAbilityScore().get()) : vm.getAbilityScore().get()
						);
	}

	/**
	 * Verifies that the ability modifier is correctly returned and formatted:
	 * 1. it can be parsed as an integer corresponding to the modifier
	 * 2. it always includes its sign ('+' for zero)
	 */
	@Test
	public void testGetAbilityModifier() {
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			testGetAbilityModifierHelper(entry.getValue(), tmpAbilities.getScore(entry.getKey()));
		}
		testGetAbilityModifierHelper(nullViewModel, tmpNullAbility);
	}
	
	/**
	 * Verifies that the ability modifier is correctly returned and formatted:
	 * 1. it can be parsed as an integer corresponding to the modifier
	 * 2. it always includes its sign ('+' for zero)
	 * @param vm		ViewModel to check
	 * @param expected	Wrapped temporary ability score of the view model.
	 */
	public void testGetAbilityModifierHelper(AbilityScoreListItemViewModel vm, AbilityScore expected) {
		String value = vm.getAbilityModifier().getValue();
		assertEquals("The ability modifier must be a string representing an integer",
				expected.getModifier(), 
				Integer.parseInt(value));
		assertTrue("The ability modifier must include its sign, received " + value 
				+ " for AbilityScore " + vm.getAbilityName(),
				value.startsWith("+")||value.startsWith("-"));
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
	 * Checks that 
	 * {@link AbilityScoreListItemViewModel#refresh()} correctly updates the
	 * observable values of the view model accordingly to the changes in the 
	 * AbilityScore and temporary AbilityScore wrapped by the view model.
	 */
	@Test
	public void testRefresh() {
		//Check without any modification
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			testRefreshHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					tmpAbilities.getScore(entry.getKey()));
		}
		testRefreshHelper(nullViewModel, nullAbility, tmpNullAbility);
		//Change temporary abilities
		EnumMap<AbilityName, Integer> values = new EnumMap<>(AbilityName.class);
		int i = ValueParameters.MIN_ABILITY_SCORE;
		for(AbilityName ability: AbilityName.values()) {
			values.put(ability, i);
			i += 4;
		}
		tmpAbilities = AbilityScores.create(values);
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			entry.getValue().setTmpAbility(tmpAbilities.getScore(entry.getKey()));
			entry.getValue().refresh();
			testRefreshHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					tmpAbilities.getScore(entry.getKey()));
		}
		//Change abilities
		creature.edit();
		for(AbilityName ability: AbilityName.values()) {
			creature.setAbilityScore(ability, tmpAbilities.getScore(ability).getValue());
		}
		creature.commit();
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			entry.getValue().refresh();
			testRefreshHelper(entry.getValue(), 
					tmpAbilities.getScore(entry.getKey()), 
					tmpAbilities.getScore(entry.getKey()));
		}
		//Nullify
		if(AbilityScores.MANDATORY_ABILITIES.contains(nullAbilityName)) {
			fail("This test assumes that " + nullAbilityName + " is not mandatory");
		}
		creature.edit();
		creature.setAbilityScore(nullAbilityName, null);
		viewModels.get(nullAbilityName).setTmpAbility(creature.getTempAbilityScores().getScore(nullAbilityName));
		viewModels.get(nullAbilityName).refresh();
		testRefreshHelper(viewModels.get(nullAbilityName), 
				creature.getAbilityScores().getScore(nullAbilityName),
				creature.getTempAbilityScores().getScore(nullAbilityName));
	}
	
	/**
	 * Checks that the view model has been correctly refreshed: 
	 * 1. it returns a String representation of its tmpAbility score
	 * 2. it returns a String representation of its tmpAbility modifier
	 * 3. it returns the right list of style classes for its tmpAbility score
	 * 4. it returns the right list of style classes for its tmpAbility modifier
	 * @param vm					to check
	 * @param expectedAbility		AbilityScore wrapped by the view model
	 * @param expectedTmpAbility	temporary AbilityScore wrapped by the 
	 * view model
	 */
	private void testRefreshHelper(AbilityScoreListItemViewModel vm, 
			AbilityScore expectedAbility, AbilityScore expectedTmpAbility) {
		testGetAbilityScoreHelper(vm, expectedTmpAbility);
		testGetAbilityModifierHelper(vm, expectedTmpAbility);
		testGetStyleClassesHelper(vm, expectedAbility, expectedTmpAbility);
	}
	
	/**
	 * Checks that 
	 * {@link AbilityScoreListItemViewModel#setTmpAbility(AbilityScore)}
	 * modifies the ability score formatted by the viewModel.
	 */
	@Test
	public void testSetTmpAbility() {
		EnumMap<AbilityName, Integer> values = AbilityScoresTest.basicAbilityScores();
		int i = 0;
		for(AbilityName ability: AbilityName.values()) {
			values.put(ability, i);
			i += 5;
		}
		AbilityScores modifiedAbilities = AbilityScores.create(values);
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			entry.getValue().setTmpAbility(modifiedAbilities.getScore(entry.getKey()));
			testRefreshHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					modifiedAbilities.getScore(entry.getKey()));
		}
		//Test de-nullification
		nullViewModel.setTmpAbility(modifiedAbilities.getScore(nullAbilityName));
		testRefreshHelper(nullViewModel, nullAbility, modifiedAbilities.getScore(nullAbilityName));
		//Test nullification
		viewModels.get(nullAbilityName).setTmpAbility(nullAbility);
		testRefreshHelper(viewModels.get(nullAbilityName), abilities.getScore(nullAbilityName), nullAbility);
	}
	
	/**
	 * Checks that the style classes returned by a view model are consistent
	 * with the ability scores it wraps.
	 */
	@Test
	public void testGetStyleClasses() {
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			testGetStyleClassesHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					tmpAbilities.getScore(entry.getKey()));
		}
		testGetStyleClassesHelper(nullViewModel, nullAbility, tmpNullAbility);
	}
	
	/**
	 * Checks that the style classes returned by a view model are consistent 
	 * with the ability scores it wraps:
	 * 1. exactly one element
	 * 2. the element is one of the String values of 
	 * {@link CreatureViewModelParameters.Modification} consistent with
	 * {@link AbilityScore#compareTo(AbilityScore)}.
	 * @param vm					to check
	 * @param expectedAbility		ability wrapped by the view model
	 * @param expectedTmpAbility	temporary ability wrapped by the view model
	 */
	private void testGetStyleClassesHelper(AbilityScoreListItemViewModel vm, 
			AbilityScore expectedAbility, AbilityScore expectedTmpAbility) {
		ObservableList<String> scoreStyle = vm.getScoreStyleClasses();
		ObservableList<String> modifierStyle = vm.getModifierStyleClasses();
		Function<Integer, String> getExpectedResult = 
				(comparison) -> {
					String expectedResult;
					if(comparison < 0) {
						expectedResult = CreatureViewModelParameters.Modification.WORSE.name();
					} else if(comparison == 0){
						expectedResult = CreatureViewModelParameters.Modification.NONE.name();
					} else {
						expectedResult = CreatureViewModelParameters.Modification.BETTER.name();
					}
					return expectedResult;
				};
		assertEquals("The score style has only one style",
				1, scoreStyle.size());
		assertEquals("The modifier style has only one style",
				1, modifierStyle.size());
		if(!expectedTmpAbility.isDefined() && expectedAbility.isDefined()) {
			assertEquals("The score style of ability " + vm.getAbilityName() + 
					" must correctly take nullification into account",
					CreatureViewModelParameters.Modification.NULLIFY.name(),
					scoreStyle.get(0));
		} else {
			assertEquals("The score style of ability " + vm.getAbilityName() +
					" must be consistent with the comparison between the temporary and the actual ability score",
					getExpectedResult.apply(expectedTmpAbility.compareTo(expectedAbility)), 
					scoreStyle.get(0));
		}
		assertEquals("The modifier style must be consistent with the comparison between the temporary and the actual modifier",
				getExpectedResult.apply(expectedTmpAbility.getModifier() - expectedAbility.getModifier()),
				modifierStyle.get(0));
	}
}
