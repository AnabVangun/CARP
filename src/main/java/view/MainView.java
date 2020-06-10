package view;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import view.creatures.CreatureView;
import viewmodel.MainViewModel;

public class MainView implements Initializable, FxmlView<MainViewModel> {
	private String creatureBundleName = "bundles.creature.creatureBundle";
	@FXML
	AnchorPane mainPane;
	
	@InjectViewModel
	MainViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
	/**
	 * Method called when the File > New > Creature button is pressed.
	 */
	public void newCreatureButtonPressed() {
		mainPane.getChildren().clear();
		mainPane.getChildren().add(
				FluentViewLoader.fxmlView(CreatureView.class)
				.viewModel(viewModel.newCreature())
				.resourceBundle(ResourceBundle.getBundle(creatureBundleName))
				.load().getView());
	}

}
