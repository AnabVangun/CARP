package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.JavaView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.FluentViewLoader.JavaViewStep;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import service.exceptions.NotYetImplementedException;
import viewmodel.creatures.AbilityScoreListItemViewModel;
import viewmodel.creatures.EditionBarItemViewModel;
import viewmodel.creatures.SimpleListViewModel;

/**
 * View used to display a simple {@link ListView} with a cell defined in an 
 * FXML.
 * This class can only be instantiated through its factory method
 * {@link SimpleListView#SimpleListViewFactory(Class, Class, SimpleListViewModel, ResourceBundle)}
 * which returns a {@link Parent} object to insert in the scene graph.
 * @author TLM
 * 
 * @param E the viewModel of the list elements.
 * @param VM the viewModel containing the data to display in the list.
 * @param V the view used to display the list elements.
 */
public class SimpleListView<E extends ViewModel, VM extends SimpleListViewModel<E>,
V extends FxmlView<E>> 
extends ListView<E> implements Initializable, JavaView<VM> {

	/**
	 * The different types of list represented by a SimpleListView.
	 * This is used to circumvent a compiler error induced by type erasure.
	 */
	public static enum ListType{
		ABILITY_SCORES(Orientation.VERTICAL),
		EDITION_BAR(Orientation.HORIZONTAL);
		
		private final Orientation orientation;
		
		private ListType(Orientation orientation) {
			this.orientation = orientation;
		}
		
		public Orientation getOrientation() {
			return this.orientation;
		}
	}

	@InjectViewModel
	private VM viewModel;

	private final Class<V> elementViewClass;

	private final Orientation orientation;
	/**
	 * Constructs an object ready to display a {@link ListView}.
	 * @param elementViewClass
	 */
	private SimpleListView(Class<V> elementViewClass, Orientation orientation) {
		this.elementViewClass = elementViewClass;
		this.orientation = orientation;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setOrientation(orientation);
		this.setItems(viewModel.getListItems());
		this.setCellFactory(CachedViewModelCellFactory.create(
				vm -> FluentViewLoader
				.fxmlView(elementViewClass)
				.viewModel(vm)
				.resourceBundle(resources)
				.load()));
	}
	/**
	 * Builds a {@link Parent} object displaying a {@link SimpleListView} 
	 * object.
	 * @param <E> view model containing data for the list elements.
	 * @param <VM> view model containing data for the list.
	 * @param <V> view displaying the list elements.
	 * @param elementViewClass class of the view displaying the list elements.
	 * @param listViewModel viewModel containing data for the list.
	 * @param resources
	 * @param orientation of the listView.
	 * @return a {@link Parent} node containing the {@link ListView}.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends ViewModel, VM extends SimpleListViewModel<E>, V extends FxmlView<E>> 
	Parent SimpleListViewFactory(ListType list, VM listViewModel, ResourceBundle resources){
		@SuppressWarnings("rawtypes")
		JavaViewStep viewStep;
		switch(list) {
		case ABILITY_SCORES:
			viewStep = FluentViewLoader.javaView(AbilityScoresList.class).codeBehind(new AbilityScoresList());
			break;
		case EDITION_BAR:
			viewStep = FluentViewLoader.javaView(EditionBarList.class).codeBehind(new EditionBarList());
			break;
		default:
			throw new NotYetImplementedException();
		}
		return viewStep
				.viewModel(listViewModel)
				.resourceBundle(resources)
				.load().getView();
	}

	private static class AbilityScoresList extends SimpleListView<AbilityScoreListItemViewModel, 
	SimpleListViewModel<AbilityScoreListItemViewModel>, AbilityScoreListItemView>{
		private AbilityScoresList() {
			super(AbilityScoreListItemView.class, Orientation.VERTICAL);
		}
	}
	
	private static class EditionBarList extends SimpleListView<EditionBarItemViewModel, 
	SimpleListViewModel<EditionBarItemViewModel>, EditionBarItemView>{
		private EditionBarList() {
			super(EditionBarItemView.class, Orientation.HORIZONTAL);
		}
	}
}