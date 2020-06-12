package view.tools;

import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * ViewTools is a helper class providing utilities designed for view classes.
 * @author TLM
 *
 */
public final class ViewTools {
	private ViewTools() {}
	
	/**
	 * Sets the content of a {@link ListView} which content is displayed in a 
	 * specialised {@link FxmlView} object.
	 * @param <E>			viewModel containing the state of each list element
	 * @param listView		list to populate.
	 * @param listData		data to display in the list.
	 * @param listItemView	view used to display each list item element.
	 * @param resources		bundle used by the list item view to display text.
	 */
	public static <E extends ViewModel> void setUpListView(ListView<E> listView, 
			ObservableList<E> listData,
			Class<? extends FxmlView<E>> listItemView,
			ResourceBundle resources) {
		listView.setItems(listData);
		listView.setCellFactory(CachedViewModelCellFactory.create(
				vm -> FluentViewLoader
				.fxmlView(listItemView)
				.viewModel((E) vm)
				.resourceBundle(resources)
				.load()));
	}
}
