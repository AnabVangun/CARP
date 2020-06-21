package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;

/**
 * Interface defining methods to expose a list of data to display to a view for
 * viewModels whose main purpose is to prepare such data.
 * @author TLM
 *
 * @param <E> the viewModel of the elements of the list.
 */
public interface SimpleListViewModel<E extends ViewModel> extends ViewModel {
	/**
	 * @return the list of viewModels containing data for each element of the 
	 * list. Depending on the implementation, this may or may not be a 
	 * read-only list.
	 */
	public ObservableList<E> getListItems();

}
