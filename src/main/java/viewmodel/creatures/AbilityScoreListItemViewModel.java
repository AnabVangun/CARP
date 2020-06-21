package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextFormatter;
import model.creatures.AbilityScores.InvalidityCode;
import model.creatures.Creature;
import model.creatures.CreatureParameters.AbilityName;
import model.values.AbilityScore;
import viewmodel.tools.TextFormatters;
import viewmodel.tools.ViewModelParameters.Styles;

/**
 * ViewModel used to display each ability score in an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoreListItemViewModel implements ViewModel {
	private final AbilityName name;
	private final Creature creature;
	private final ReadOnlyStringWrapper score = new ReadOnlyStringWrapper();
	private ReadOnlyStringWrapper modifier = new ReadOnlyStringWrapper();
	/** This boolean controls whether the score can be modified by the user. */
	private final ObservableBooleanValue canEditScore;
	/** This boolean controls whether the ability score is null. */
	private boolean isNotNull;
	/**List of the style classes to apply to the ability score.*/
	private final ReadOnlyListWrapper<String> scoreStyles = 
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	/**List of the style classes to apply to the ability modifier.*/
	private final ReadOnlyListWrapper<String> modifierStyles = 
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	/**
	 * Indices of the different types of styles that may be applied to the 
	 * elements displaying an {@link AbilityScoreListItemViewModel}.
	 */
	private static enum StyleIndex{
		MODIFICATION_STATUS(0),
		EDITABLE_STATUS(1),
		VALIDITY_STATUS(2);
		private final int i;
		private StyleIndex(int i) {
			this.i = i;
		}
		public int getIndex() {
			return this.i;
		}
	}
	private InvalidationListener editabilityListener = (observable) -> this.refreshEditabilityStyle();
	
	public AbilityScoreListItemViewModel(AbilityName name, Creature creature,
			ObservableBooleanValue isModifiable) {
		if(name == null || creature == null || isModifiable == null) {
			throw new NullPointerException("Cannot instantiate an ability score view model on a null creature");
		}
		this.name = name;
		this.creature = creature;
		this.scoreStyles.addAll(null, null, null);
		this.modifierStyles.add(null);
		this.canEditScore = isModifiable;
		refresh();
		refreshEditabilityStyle();
		this.canEditScore.addListener(new WeakInvalidationListener(editabilityListener));
	}
	
	
	/**
	 * Sets all the properties derived from the underlying {@link Creature}
	 * object.
	 */
	public void refresh() {
		AbilityScore ability = creature.getAbilityScores().getScore(name);
		AbilityScore tmpAbility = creature.getTempAbilityScores().getScore(name);
		isNotNull = tmpAbility.isDefined();
		if(isNotNull) {
			score.set(String.format("%,d", tmpAbility.getValue()));
		} else {
			score.set("");
		}
		modifier.set(String.format("%+,d", tmpAbility.getModifier()));
		this.scoreStyles.set(StyleIndex.MODIFICATION_STATUS.getIndex(),
				(ability.isDefined() && !tmpAbility.isDefined()) ?
						Styles.NULLIFY.name() :
						computeModificationStatus(tmpAbility.compareTo(ability)));
		this.modifierStyles.set(StyleIndex.MODIFICATION_STATUS.getIndex(),
				computeModificationStatus(tmpAbility.getModifier() - ability.getModifier()));
	}
	
	/**
	 * Computes the modification status based on the comparison between two 
	 * ability scores or modifiers. Does not handle nullification of the 
	 * ability score.
	 * @param comparison difference between the tmpAbility and the ability of
	 * the {@link Creature}, be it in their score or their modifier.
	 * @return	the name of the relevant style class to highlight the 
	 * comparison.
	 */
	private String computeModificationStatus(int comparison) {
		Styles modifStatus;
		if(comparison < 0) {
			modifStatus = Styles.WORSE;
		} else if (comparison == 0) {
			modifStatus = Styles.NONE;
		} else {
			modifStatus = Styles.BETTER;
		}
		return modifStatus.name();
	}
	
	/**
	 * @return the name of the ability as a read-only observable.
	 */
	public String getAbilityName() {
		return name.toString();
	}
	
	/**
	 * @return the score of the ability as a read-write observable.
	 */
	public ObservableStringValue getAbilityScore() {
		return score.getReadOnlyProperty();
	}
	
	/**
	 * @return a {@link TextFormatter} object rejecting all non-integer inputs.
	 */
	public TextFormatter<Integer> getAbilityScoreFormatter(){
		return TextFormatters.getIntegerFormatter(isNotNull ? 
				creature.getTempAbilityScores().getScore(name).getValue() : null);
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
	public ObservableBooleanValue isScoreEditable() {
		return canEditScore;
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
	
	/**
	 * Adds the style {@link ViewModelParameters.Styles#EDITABLE} if the score
	 * is editable, and removes it otherwise.
	 */
	private void refreshEditabilityStyle() {
		this.scoreStyles.set(StyleIndex.EDITABLE_STATUS.getIndex(),
				canEditScore.get() ? Styles.EDITABLE.name() : null);
	}
	
	/**
	 * Sets the score of the {@link AbilityScore} formatted by this object 
	 * and updates its style accordingly.
	 * @param value	may be null to empty the parameter
	 */
	public void setScore(Integer value) {
		//TODO use the result to display more information to the user
		InvalidityCode result = creature.setAbilityScore(this.name, value);
		this.scoreStyles.set(StyleIndex.VALIDITY_STATUS.getIndex(), 
				result == null ? Styles.VALID.name() : Styles.INVALID.name());
		this.refresh();
	}
}
