package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;

/**
 * Interface offering methods to be used by a 
 * {@link view.creatures.SimpleListView} to collect data from its viewModel to
 * display the content of the list.
 * @author TLM
 *
 * @param <E> the viewModel of the elements of the list.
 */
public interface SimpleListViewModel<E extends ViewModel> extends ViewModel {
	/**
	 * @return the list of viewModels containing data for each element of the 
	 * list.
	 */
	public ObservableList<E> getListItems();

}
