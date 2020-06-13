package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import view.tools.ViewTools;
import viewmodel.creatures.AbilityScoreListItemViewModel;
import viewmodel.creatures.CreatureEditionViewModel;
import viewmodel.creatures.CreatureViewModel;

public class CreatureView implements Initializable, FxmlView<CreatureViewModel>{
	@FXML
	VBox root;
	@FXML
	GridPane container;
	@FXML
	ListView<AbilityScoreListItemViewModel> abilities;
	@InjectViewModel
	CreatureViewModel viewModel;
	ResourceBundle resources;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		AnchorPane.setTopAnchor(root, 0.0);
		AnchorPane.setBottomAnchor(root, 0.0);
		AnchorPane.setLeftAnchor(root, 0.0);
		AnchorPane.setRightAnchor(root, 0.0);
		this.resources = resources;
		setUpEditionBar();
		//Include ability scores in the creature sheet
		ViewTools.setUpListView(abilities, 
				viewModel.getAbilities().get().getListItems(), 
				AbilityScoreListItemView.class, 
				resources);
	}
	
	private void setUpEditionBar() {
		//Build edition bar
		Parent editionBar = FluentViewLoader.fxmlView(CreatureEditionView.class)
				.viewModel(new CreatureEditionViewModel(viewModel.getCurrentEditionPhase()))
				.resourceBundle(resources)
				.resourceBundle(ResourceBundle.getBundle("bundles.creature.CreatureEditionBundle"))
				.load().getView();
		//Display edition bar if and only if the creature is being edited
		editionBar.managedProperty().bind(editionBar.visibleProperty());
		editionBar.visibleProperty().bind(viewModel.isInEditMode());
		//Add edition bar to the layout
		root.getChildren().add(0, editionBar);
	}

}
