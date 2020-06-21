package viewmodel.creatures;

import static org.junit.Assert.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import model.creatures.AbilityScoresTest;
import model.creatures.Creature;
import model.creatures.CreatureTest;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import model.values.ValueParameters;
import viewmodel.tools.ViewModelParameters;
import viewmodel.tools.ViewModelParameters.Styles;

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
	private Creature nullCreature;
	private SimpleBooleanProperty isModifiable = new SimpleBooleanProperty(true);
	

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
		creature.edit();
		viewModels = new EnumMap<AbilityName, AbilityScoreListItemViewModel>(AbilityName.class);
		for(Map.Entry<AbilityName, AbilityScore> entry: tmpAbilities) {
			creature.setAbilityScore(entry.getKey(), entry.getValue().getValue());
			viewModels.put(entry.getKey(),
					new AbilityScoreListItemViewModel(entry.getKey(), creature,
									isModifiable));
		}
		AbilityScores nullAbilities = AbilityScores.create(null);
		nullAbilityName = AbilityName.STRENGTH;
		nullAbility = nullAbilities.getScore(nullAbilityName);
		tmpNullAbility = tmpAbilities.getScore(nullAbilityName);
		nullCreature = new Creature();
		nullCreature.setAbilityScore(nullAbilityName, tmpNullAbility.getValue());
		nullViewModel = new AbilityScoreListItemViewModel(nullAbilityName,
				nullCreature, isModifiable);
	}

	/**
	 * Verifies that the constructor throws an exception when either argument 
	 * is null.
	 */
	@Test
	public void testNullInput() {
		for(AbilityName name : new AbilityName[] {nullAbilityName, null}) {
			for(Creature creature : new Creature[] {creature, nullCreature, null}) {
				for(ObservableBooleanValue value : new ObservableBooleanValue[] {isModifiable, null}) {
					if(name != null && creature != null && value != null) {
						continue;
					} else {
						try {
							new AbilityScoreListItemViewModel(name, creature, value);
							fail("An AbilityScoreListItemViewModel cannot accept a null parameter, "
									+ "failed to throw exception with null parameter(s) "
									+ (name == null ? "name, " : "")
									+ (creature == null ? "ability, " : "")
									+ (value == null ? "isModifiable" : ""));
						} catch (NullPointerException e) {}
					}
				}
			}
		}
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
	 * {@link AbilityScoreListItemViewModel#isScoreEditable()} method returns
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
		ObservableBooleanValue value = viewModel.isScoreEditable();
		assertEquals("By default, a score is not modifiable",
				isModifiable.get(),
				viewModel.isScoreEditable().get());
		isModifiable.set(true);
		assertTrue("After setting it to true, the value is true",
				viewModel.isScoreEditable().get());
		assertTrue("The Observable should be updated to true", value.get());
		isModifiable.set(false);
		assertFalse("After setting it to false, the value is false",
				viewModel.isScoreEditable().get());
		assertFalse("The Observable should be updated to false", value.get());
		isModifiable.set(false);
		assertFalse("Setting to false a false variable should not be an issue",
				viewModel.isScoreEditable().get());
	}
	
	/**
	 * Checks that 
	 * {@link AbilityScoreListItemViewModel#refresh()} correctly updates the
	 * observable values of the view model accordingly to the changes in the 
	 * AbilityScore and temporary AbilityScore wrapped by the view model.
	 */
	@Test
	public void testRefresh() {
		creature.edit();
		//Check without any modification
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			testRefreshHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					tmpAbilities.getScore(entry.getKey()));
		}
		testRefreshHelper(nullViewModel, nullAbility, tmpNullAbility);
		//Change temporary abilities
		int i = ValueParameters.MIN_ABILITY_SCORE;
		for(AbilityName ability: AbilityName.values()) {
			creature.setAbilityScore(ability, i);
			i += 4;
		}
		for(Map.Entry<AbilityName, AbilityScoreListItemViewModel> entry : viewModels.entrySet()) {
			entry.getValue().refresh();
			testRefreshHelper(entry.getValue(), 
					abilities.getScore(entry.getKey()), 
					creature.getTempAbilityScores().getScore(entry.getKey()));
		}
		//Change abilities
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
	 * {@link ViewModelParameters.Styles} consistent with
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
						expectedResult = ViewModelParameters.Styles.WORSE.name();
					} else if(comparison == 0){
						expectedResult = ViewModelParameters.Styles.NONE.name();
					} else {
						expectedResult = ViewModelParameters.Styles.BETTER.name();
					}
					return expectedResult;
				};
		assertEquals("The score style has exactly three styles",
				3, scoreStyle.size());
		assertEquals("The modifier style has only one style",
				1, modifierStyle.size());
		if(!expectedTmpAbility.isDefined() && expectedAbility.isDefined()) {
			assertEquals("The score style of ability " + vm.getAbilityName() + 
					" must correctly take nullification into account",
					ViewModelParameters.Styles.NULLIFY.name(),
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
		if(vm.isScoreEditable().get()) {
			assertEquals("If the score is editable, the editable style must be applied",
					ViewModelParameters.Styles.EDITABLE.name(),
					scoreStyle.get(1));
		} else {
			assertFalse("If the score is not editable, the editable style must not be applied",
					scoreStyle.contains(ViewModelParameters.Styles.EDITABLE.name()));
		}
	}
	/**
	 * Checks that {@link AbilityScoreListItemViewModel#setScore(Integer)}
	 * nullifies the tmpAbility of the {@link Creature} object on a null input.
	 */
	@Test
	public void setScore_Nullify() {
		viewModels.get(nullAbilityName).setScore(null);
		assertFalse("Set score on a null input nullifies the ability score",
				creature.getTempAbilityScores().getScore(nullAbilityName).isDefined());
	}
	/**
	 * Checks that {@link AbilityScoreListItemViewModel#setScore(Integer)}
	 * sets the input value as the tmpAbility of the {@link Creature} object on
	 * a valid input.
	 */
	@Test
	public void setScore_AssignValidValue() {
		//Make sure to pick a value that is different from the current one.
		int expected = prepareValidValue(nullAbilityName);
		viewModels.get(nullAbilityName).setScore(expected);
		assertEquals("Set score must modify the tmp ability score of the creature",
				expected, 
				creature.getTempAbilityScores().getScore(nullAbilityName).getValue());
	}
	/**
	 * Checks that {@link AbilityScoreListItemViewModel#setScore(Integer)}
	 * sets the valid style on a valid input.
	 */
	@Test
	public void setScore_StyleValidValue() {
		int expected = prepareValidValue(nullAbilityName);
		viewModels.get(nullAbilityName).setScore(expected);
		assertTrue("After a valid setScore, the style must be valid",
				viewModels.get(nullAbilityName).getScoreStyleClasses().contains(Styles.VALID.name())
				&& ! viewModels.get(nullAbilityName).getScoreStyleClasses().contains(Styles.INVALID.name()));
	}
	
	/**
	 * Return a value that is valid but different from the temporary one of the
	 * creature.
	 * @param name of the ability to use as a basis to compute the result.
	 * @return	an integer between MIN_ABILITY_SCORE and MAX_ABILITY_SCORE
	 */
	private int prepareValidValue(AbilityName name) {
		int result = creature.getTempAbilityScores().getScore(name).getValue() - 3;
		while(result < ValueParameters.MIN_ABILITY_SCORE) {
			result += 7;
		}
		return result;
	}
	/**
	 * Checks that {@link AbilityScoreListItemViewModel#setScore(Integer)}
	 * sets the input value as the tmpAbility of the {@link Creature} object on
	 * an invalid input.
	 */
	@Test
	public void setScore_AssignInvalidValue() {
		int expected = ValueParameters.MAX_ABILITY_SCORE + 2;
		viewModels.get(nullAbilityName).setScore(expected);
		assertEquals("Set score must modify the tmp ability score of the creature",
				expected, 
				creature.getTempAbilityScores().getScore(nullAbilityName).getValue());
	}
	/**
	 * Checks that {@link AbilityScoreListItemViewModel#setScore(Integer)}
	 * sets the invalid style on an invalid input.
	 */
	@Test
	public void setScore_StyleInvalidValue() {
		int expected = ValueParameters.MIN_ABILITY_SCORE - 2;
		viewModels.get(nullAbilityName).setScore(expected);
		assertTrue("After a valid setScore, the style must be valid",
				! viewModels.get(nullAbilityName).getScoreStyleClasses().contains(Styles.VALID.name())
				&& viewModels.get(nullAbilityName).getScoreStyleClasses().contains(Styles.INVALID.name()));
	}
	
	
}
