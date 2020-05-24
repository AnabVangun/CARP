package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import viewmodel.creatures.CreatureViewModel;
import viewmodel.creatures.EditionBarViewModel;

public class CreatureView implements Initializable, FxmlView<CreatureViewModel>{
	@FXML
	VBox root;
	@FXML
	GridPane container;
	@InjectViewModel
	CreatureViewModel viewModel;
	ResourceBundle resources;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		setUpEditionBar();
		//Include ability scores in the creature sheet
		container.add(SimpleListView.SimpleListViewFactory(
				SimpleListView.ListType.ABILITY_SCORES,
				viewModel.getAbilities().get(), 
				resources), 
				0, 0);
	}
	
	private void setUpEditionBar() {
		//Build edition bar
		//TODO replace null by the current phase index
		Parent editionBar = SimpleListView.SimpleListViewFactory(SimpleListView.ListType.EDITION_BAR, 
				new EditionBarViewModel(null), resources);
		//Display edition bar if and only if the creature is being edited
		editionBar.managedProperty().bind(editionBar.visibleProperty());
		editionBar.visibleProperty().bind(viewModel.isInEditMode());
		//Add edition bar to the layout
		root.getChildren().add(0, editionBar);
	}

}
