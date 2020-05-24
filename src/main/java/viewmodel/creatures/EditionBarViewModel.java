package viewmodel.creatures;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.Creature;

/**
 * ViewModel used to manage the edition bar when editing a {@link Creature}.
 * This viewModel is read-only: the data cannot be modified, only its display 
 * style may update itself.
 * @author TLM
 */
public class EditionBarViewModel implements SimpleListViewModel<EditionBarItemViewModel> {
	private ReadOnlyListWrapper<EditionBarItemViewModel> itemViewModels;
	/**
	 * Initialises a view model to display the edition bar for the creature view
	 * @param currentPhaseIndex
	 */
	public EditionBarViewModel(ObservableIntegerValue currentPhaseIndex) {
		int i = 0;
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
}
