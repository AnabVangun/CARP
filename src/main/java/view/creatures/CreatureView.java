package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import viewmodel.creatures.CreatureEditionViewModel;
import viewmodel.creatures.CreatureViewModel;

public class CreatureView implements Initializable, FxmlView<CreatureViewModel>{
	@FXML
	private VBox root;
	@FXML
	private GridPane container;
	@FXML
	private VBox abilities;
	@InjectViewModel
	private CreatureViewModel viewModel;
	private ResourceBundle resources;
	private AbilityScoresViewHandler abilitiesHandler;
	/**
	 * The different parts of the creature on display.
	 */
	protected static enum CreaturePart{
		ABILITIES;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AnchorPane.setTopAnchor(root, 0.0);
		AnchorPane.setBottomAnchor(root, 0.0);
		AnchorPane.setLeftAnchor(root, 0.0);
		AnchorPane.setRightAnchor(root, 0.0);
		this.resources = resources;
		//Include ability scores in the creature sheet
		abilitiesHandler = new AbilityScoresViewHandler(abilities, viewModel.getAbilities().get(), resources);
		setUpEditionBar();
	}
	
	/**
	 * Sets up the {@link CreatureEditionView} object for this view.
	 */
	private void setUpEditionBar() {
		//Build edition bar
		Parent editionBar = FluentViewLoader.fxmlView(CreatureEditionView.class)
				.codeBehind(new CreatureEditionView(this))
				.viewModel(new CreatureEditionViewModel(viewModel))
				.resourceBundle(resources)
				.resourceBundle(ResourceBundle.getBundle("bundles.creature.CreatureEditionBundle"))
				.load().getView();
		//Display edition bar if and only if the creature is being edited
		editionBar.managedProperty().bind(viewModel.isInEditMode());
		editionBar.visibleProperty().bind(editionBar.managedProperty());//XXX unclear if this really is necessary
		//Add edition bar to the layout
		root.getChildren().add(0, editionBar);
	}
	
	/**
	 * Forges a copy of the requested part of the creature bound to the same
	 * view model as the original {@link Node}.
	 * @param part	of the creature to copy
	 * @return	a {@link Node} displaying the selected part of the creature.
	 */
	public Node getCreaturePart(CreaturePart part) {
		switch(part) {
		case ABILITIES:
			VBox result = new VBox();
			abilitiesHandler.fillContainer(result);
			return result;
		default:
			return null;
		}
	}
}
