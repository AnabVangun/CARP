package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import viewmodel.creatures.CreatureViewModelParameters.Modification;

/**
 * ViewModel used to display each ability score in an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoreListItemViewModel implements ViewModel {
	public AbilityScoreListItemViewModel(AbilityName name, AbilityScore ability, AbilityScore tmpAbility) {
		if(ability == null || tmpAbility == null) {
			throw new NullPointerException("Cannot instantiate an ability score view model on a null ability");
		}
		this.name = name.toString();
		this.ability = ability;
		this.tmpAbility = tmpAbility;
		this.scoreStyles.add("");
		this.modifierStyles.add("");
		refresh();
	}
	
	private AbilityScore ability;
	private AbilityScore tmpAbility;
	private final String name;
	private final StringProperty score = new SimpleStringProperty();
	private ReadOnlyStringWrapper modifier = new ReadOnlyStringWrapper();
	/** This boolean controls whether the score can be modified by the user. */
	private final BooleanProperty canEditScore = new SimpleBooleanProperty(false);
	/** This boolean controls whether the ability score is null. */
	private boolean isNotNull;
	/**List of the style classes to apply to the ability score.*/
	private final ReadOnlyListWrapper<String> scoreStyles = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	/**List of the style classes to apply to the ability modifier.*/
	private final ReadOnlyListWrapper<String> modifierStyles = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	private static final int MODIFICATION_INDEX = 0;
	
	/**
	 * This sets all the properties derived from the temporary ability score.
	 */
	public void refresh() {
		isNotNull = this.tmpAbility.isDefined();
		if(isNotNull) {
			score.set(String.format("%,d", this.tmpAbility.getValue()));
		} else {
			score.set("");
		}
		modifier.set(String.format("%+,d", this.tmpAbility.getModifier()));
		this.scoreStyles.set(MODIFICATION_INDEX,
				(this.ability.isDefined() && !this.tmpAbility.isDefined()) ?
						Modification.NULLIFY.name() :
						computeModificationStatus(this.tmpAbility.compareTo(this.ability)));
		this.modifierStyles.set(MODIFICATION_INDEX,
				computeModificationStatus(this.tmpAbility.getModifier() - this.ability.getModifier()));
	}
	
	/**
	 * Computes the modification status based on the comparison between two 
	 * ability scores or modifiers. Does not handle nullification of the 
	 * ability score.
	 * @param comparison result of tmpAbility.compareTo(ability) or
	 * tmpAbility.getModifier() - ability.getModifier()
	 * @return	the name of the Modification status to include in the styles.
	 */
	private String computeModificationStatus(int comparison) {
		Modification modifStatus;
		if(comparison < 0) {
			modifStatus = Modification.WORSE;
		} else if (comparison == 0) {
			modifStatus = Modification.NONE;
		} else {
			modifStatus = Modification.BETTER;
		}
		return modifStatus.name();
	}
	
	/**
	 * @return the name of the ability as a read-only observable.
	 */
	public String getAbilityName() {
		return name;
	}
	
	/**
	 * @return the score of the ability as a read-write observable.
	 */
	public ObservableStringValue getAbilityScore() {
		return score;
	}
	
	/**
	 * @return the modifier of the ability as a read-only observable String:
	 * it always contain a sign (0 is considered positive).
	 */
	public ObservableStringValue getAbilityModifier() {
		return modifier.getReadOnlyProperty();
	}
	
	/**
	 * @return an observable boolean that is true if the score field can be 
	 * edited.
	 */
	public ObservableBooleanValue isScoreModifiable() {
		return canEditScore;
	}
	
	/**
	 * Set the modifiability of the score to a given value.
	 * @param value
	 */
	public void setIsScoreModifiable(boolean value) {
		canEditScore.set(value);
	}
	
	/**
	 * Sets the temporary ability displayed by the object and refresh the 
	 * object.
	 * @param tmpAbility	may be the undefined ability but cannot be null
	 * @throws NullPointerException if the input is null.
	 */
	protected void setTmpAbility(AbilityScore tmpAbility) {
		if(tmpAbility == null) {
			throw new NullPointerException("Cannot set null as a temporary ability");
		}
		this.tmpAbility = tmpAbility;
		refresh();
	}
	
	/**
	 * @return the list of styles to apply to the score field.
	 */
	public ObservableList<String> getScoreStyleClasses(){
		return this.scoreStyles.getReadOnlyProperty();
	}
	
	/**
	 * @return the list of styles to apply to the modifier field.
	 */
	public ObservableList<String> getModifierStyleClasses(){
		return this.modifierStyles.getReadOnlyProperty();
	}
}
