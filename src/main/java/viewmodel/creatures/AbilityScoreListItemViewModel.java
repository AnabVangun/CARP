package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import model.values.AbilityScore;
import service.parameters.CreatureParameters.AbilityName;

/**
 * ViewModel used to display each ability score in an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoreListItemViewModel implements ViewModel {
	public AbilityScoreListItemViewModel(AbilityName name, AbilityScore ability) {
		this.name = name.toString();
		setAbilityProperties(ability);
	}
	
	private AbilityScore ability;
	private final String name;
	private final StringProperty score = new SimpleStringProperty();
	private ReadOnlyStringWrapper modifier = new ReadOnlyStringWrapper();
	/** This boolean controls whether the score can be modified by the user. */
	private final BooleanProperty canEditScore = new SimpleBooleanProperty(false);
	/** This boolean controls whether the ability score is null. */
	private final BooleanProperty isNotNull = new SimpleBooleanProperty(false);
	
	/**
	 * This sets all the properties derived from the ability score.
	 * @param ability
	 */
	private void setAbilityProperties(AbilityScore ability) {
		this.ability = ability;
		isNotNull.set(this.ability != null);
		if(isNotNull.get()) {
			score.set(String.format("%,d", this.ability.getValue()));
		}
		modifier.set(String.format("%+,d", isNotNull.get()?ability.getModifier():0));
	}
	
	/**
	 * @return the name of the ability as a read-only observable.
	 */
	public String getAbilityName() {
		return name;
	}
	
	/**
	 * @return the score of the ability as a read-write observable.
	 * @throws {@link NullPointerException} if the ability is not defined.
	 */
	public ObservableStringValue getAbilityScore() {
		if(isScoreNotNull().get()) {
			return score;
		} else {
			throw new NullPointerException("Cannot get the score of a null ability");
		}
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
	 * @return true if the score field is not null, and false if it is null.
	 */
	public ObservableBooleanValue isScoreNotNull() {
		return this.isNotNull;
	}
}
