package view.creatures;

import java.util.ResourceBundle;
import de.saxsys.mvvmfx.FluentViewLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import viewmodel.creatures.AbilityScoresViewModel;

/**
 * Handler used to fill a container with the data from an 
 * {@link AbilityScoresViewModel} object, and generate duplicates of the node
 * on demand.
 * @author TLM
 *
 */
public class AbilityScoresViewHandler {
	
	private final AbilityScoresViewModel vm;
	private final ResourceBundle resources;
	private final Pane container;
	
	public AbilityScoresViewHandler(Pane container, 
			AbilityScoresViewModel data, ResourceBundle resources) {
		this.container = container;
		this.vm = data;
		this.resources = resources;
		setUpView(this.container);
	}
	
	/**
	 * Fills the container with nodes representing the data.
	 */
	private void setUpView(Pane container){
		container.getChildren().setAll(this.vm.getListItems().stream()
				.map(vm -> FluentViewLoader
						.fxmlView(AbilityScoreListItemView.class)
						.viewModel(vm)
						.resourceBundle(resources)
						.load().getView()).toArray(Node[]::new)
				);
	}
	
	/**
	 * Fills the input container with the content of this object.
	 * @param container to set up.
	 */
	public void fillContainer(Pane container) {
		setUpView(container);
	}

}
