package viewmodel.creatures;

import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.Creature;

public class EditionBarViewModel implements SimpleListViewModel<EditionBarItemViewModel> {
	private IntegerProperty currentPhaseIndex;
	//private ReadOnlyListWrapper<EditionBarItemViewModel> itemViewModels = new ReadOnlyListWrapper<>();
	private ObservableList<EditionBarItemViewModel> itemViewModels = FXCollections.observableArrayList();
	/**
	 * Initialises a view model to display the edition bar for the creature view
	 * @param currentPhaseIndex
	 */
	public EditionBarViewModel(ObservableIntegerValue currentPhaseIndex) {
		//this.currentPhaseIndex.bind(currentPhaseIndex);
		for(Creature.InitStatus status : Creature.EDITION_STATUSES) {
			EditionBarItemViewModel tmp = new EditionBarItemViewModel(status.toString());
			itemViewModels.add(tmp);
		}
	}
	
	/**
	 * @return the viewModels containing the data to display in each element of
	 * the edition bar.
	 */
	//public ReadOnlyListProperty<EditionBarItemViewModel> getItemViewModels(){
	@Override
	public ObservableList<EditionBarItemViewModel> getListItems() {
		//return itemViewModels.getReadOnlyProperty();
		return this.itemViewModels;
	}
}
