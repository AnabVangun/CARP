package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import viewmodel.creatures.AbilityScoresViewModel;
import viewmodel.creatures.CreatureViewModel;

public class CreatureView implements Initializable, FxmlView<CreatureViewModel>{

	@FXML
	GridPane container;
	@InjectViewModel
	CreatureViewModel viewModel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Include ability scores in the creature sheet
		ViewTuple<AbilityScoresView, AbilityScoresViewModel> abilityViewTuple = 
        		FluentViewLoader
        		.fxmlView(AbilityScoresView.class)
        		.viewModel(viewModel.getAbilities().get())
        		.load();
		container.getChildren().add(abilityViewTuple.getView());
	}

}
