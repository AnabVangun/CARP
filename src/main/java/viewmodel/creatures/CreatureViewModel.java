package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;
import model.creatures.Creature;

/**
 * View model used to display a {@link Creature} object.
 * @author TLM
 */
public class CreatureViewModel implements ViewModel{
	//TODO add interface/abstract class for the refresh method for all viewmodels that may become dirty
	private ReadOnlyObjectWrapper<AbilityScoresViewModel> abilities = new ReadOnlyObjectWrapper<>();
	private ReadOnlyBooleanWrapper isEditing = new ReadOnlyBooleanWrapper();
	private Creature creature;
	private ReadOnlyIntegerWrapper currentPhaseIndex = new ReadOnlyIntegerWrapper();
	/**
	 * Initialises a {@link CreatureViewModel} object to display a 
	 * {@link Creature object}.
	 * @param creature object to display. Cannot be null.
	 * @throws a NullPointerException if the creature is null.
	 */
	public CreatureViewModel(Creature creature) {
		if(creature == null) {
			throw new NullPointerException("Tried to initialise a CreatureViewModel on a null creature");
		}
		this.creature = creature;
		this.refresh();
	}
	
	/**
	 * @return a viewModel to display the ability scores of the creature.
	 */
	public ObservableObjectValue<AbilityScoresViewModel> getAbilities(){
		return this.abilities.getReadOnlyProperty();
	}
	
	/**
	 * @return true if the creature on display is being edited, false if it is complete.
	 */
	public ObservableBooleanValue isInEditMode() {
		return this.isEditing.getReadOnlyProperty();
	}
	
	/**
	 * Refresh the observable fields from the model.
	 * This method should be called if the underlying {@link Creature} object 
	 * is modified from outside of this object.
	 */
	public void refresh() {
		this.abilities.set(new AbilityScoresViewModel(creature.getAbilityScores()));
		this.isEditing.set(!creature.isInitialised());
		this.currentPhaseIndex.set(creature.getInitialisationStatus().ordinal());
	}
	
	/**
	 * @return the index of the current phase of edition of the creature 
	 * on display in the list returned by 
	 * {@link Creature#EDITION_STATUSES}, or the length of the 
	 * list if the creature is fully initialised.
	 */
	public ObservableIntegerValue getCurrentEditionPhase() {
		return currentPhaseIndex.getReadOnlyProperty();
	}
	
	//TODO delete after test
	@Deprecated
	public void changeCreature() {
		creature.finish();
		refresh();
	}
}
