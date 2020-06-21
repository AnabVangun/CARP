package view.tools;

import java.util.ResourceBundle;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.beans.binding.ListBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * ViewTools is a helper class providing utilities designed for view classes.
 * @author TLM
 *
 */
public final class ViewTools {
	/** Path to the bundle containing the creature resources. */
	public final static String CREATURE_BUNDLE_PATH = "bundles.creature.creatureBundle";
	/** Path to the bundle containing the input prompt resources. */
	public final static String INPUT_BUNDLE_PATH = "bundles.menu.inputBundle";
	/** Path to the bundle containing the creature edition resources. */
	public final static String CREATURE_EDITION_BUNDLE_PATH = "bundles.creature.CreatureEditionBundle";
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
	/**
	 * Sets the text labels in the given {@link ListView} using the provided
	 * internationalised resources.
	 * @param listView	to update.
	 * @param data		to set in the listView. Must correspond to the keys to
	 * fetch in the resource bundle.
	 * @param resources	in which the internationalised text is kept.
	 */
	public static void setI18nListItems(ListView<String> listView, 
			ObservableList<String> data,
			ResourceBundle resources) {
		listView.setItems(new ListBinding<String>() {
			{super.bind(data);}
			@Override
			protected ObservableList<String> computeValue() {
				return FXCollections.observableArrayList((data.stream()
				.map(value -> resources.getString(value))
				.toArray(String[]::new)));
			}
		});
	}
}
