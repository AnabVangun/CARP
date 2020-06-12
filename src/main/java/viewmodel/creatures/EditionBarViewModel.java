package viewmodel.creatures;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.Creature;
import service.exceptions.NotYetImplementedException;

/**
 * ViewModel used to manage the edition bar when editing a {@link Creature}.
 * This viewModel is read-only: the data cannot be modified, only its display 
 * style may update itself.
 * @author TLM
 */
public class EditionBarViewModel implements SimpleListViewModel<EditionBarItemViewModel> {
	private ReadOnlyListWrapper<EditionBarItemViewModel> itemViewModels;
	private ObservableIntegerValue currentPhaseIndex;
	/**
	 * Initialises a view model to display the edition bar for the creature view
	 * @param currentPhaseIndex
	 */
	public EditionBarViewModel(ObservableIntegerValue currentPhaseIndex) {
		int i = 0;
		this.currentPhaseIndex = currentPhaseIndex;
		List<EditionBarItemViewModel> init = new ArrayList<>();
		for(Creature.InitStatus status : Creature.EDITION_STATUSES) {
			init.add(new EditionBarItemViewModel(status.toString(), currentPhaseIndex, i));
			i++;
		}
		itemViewModels = new ReadOnlyListWrapper<>(FXCollections.observableList(init));
	}
	
	@Override
	public ObservableList<EditionBarItemViewModel> getListItems() {
		return itemViewModels.getReadOnlyProperty();
	}
	
	/**
	 * Returns the key to get the description of the current step in the 
	 * resource bundle. This method returns a String and not an Observable 
	 * because it only changes when the current step changes.
	 * @return the key to get the description of the current edition step.
	 */
	public String getStepDescriptionKey() {
		throw new NotYetImplementedException(); //TODO test and implement
	}
}
