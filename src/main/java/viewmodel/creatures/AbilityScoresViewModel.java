package viewmodel.creatures;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.Creature;
import model.creatures.CreatureParameters;
import model.creatures.CreatureParameters.AbilityName;
import service.exceptions.NotYetImplementedException;

/**
 * ViewModel used to display an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoresViewModel implements SimpleListViewModel<AbilityScoreListItemViewModel> {
	private final ReadOnlyListWrapper<AbilityScoreListItemViewModel> abilityList;
	private final ReadOnlyBooleanWrapper areScoresEditable = new ReadOnlyBooleanWrapper(false);
	private final ObservableBooleanValue isCreatureEditable;
	private final InvalidationListener creatureEditabilityListener;
	
	public AbilityScoresViewModel(Creature creature,
			ObservableBooleanValue isCreatureEditable) {
		if(creature == null || isCreatureEditable == null) {
			throw new NullPointerException("Cannot instantiate an AbilityScoresViewModel on null abilities");
		}
		List<AbilityScoreListItemViewModel> init = new ArrayList<>();
		for(AbilityName ability : AbilityName.values()) {
			init.add(new AbilityScoreListItemViewModel(ability, 
					creature,
					this.areScoresEditable.getReadOnlyProperty()));
		}
		abilityList = new ReadOnlyListWrapper<>(FXCollections.observableArrayList(init));
		this.isCreatureEditable = isCreatureEditable;
		creatureEditabilityListener = (Observable observable) 
				-> {if(!this.isCreatureEditable.get()) {reset();}};
		this.isCreatureEditable.addListener(new WeakInvalidationListener(creatureEditabilityListener));
	}
	/**
	 * @return the ability scores as an observable list, where each 
	 * {@link model.creatures.CreatureParameters.AbilityName} is represented
	 * by a viewModel, even if it is null.
	 */
	@Override
	public ObservableList<AbilityScoreListItemViewModel> getListItems() {
		return abilityList.getReadOnlyProperty();
	}
	
	/**
	 * Sets the method used to generate or modify the ability scores of the 
	 * creature. This method must be called one final time with null to mark
	 * the end of the modification of the AbilityScores. This method fails
	 * if the creature is not editable.
	 * @param method	may be null to indicate that the AbilityScores are not 
	 * being edited.
	 * @throws IllegalStateException	when this method is called while the
	 * creature is not editable.
	 */
	public void setGenerationMethod(CreatureParameters.AbilityGenerationMethod method) throws IllegalStateException{
		if(!isCreatureEditable.get()) {
			throw new IllegalStateException("Tried to set the generation method for ability scores for a non-editable creature");
		}
		this.reset();
		if(method == null) {
			return;
		}
		switch(method) {
		case DIRECT_ASSIGNMENT:
			this.areScoresEditable.set(true);
			break;
		case STANDARD:
		case DICE_POOL:
			//TODO test and implement
			break;
		default:
			throw new NotYetImplementedException();
		}
	}
	
	/**
	 * Makes the object leave the edition state.
	 * This method is called automatically when the creature becomes not 
	 * editable.
	 */
	private void reset() {
		this.areScoresEditable.set(false);
	}
}
