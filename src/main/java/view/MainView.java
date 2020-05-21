package view;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import viewmodel.MainViewModel;

public class MainView implements Initializable, FxmlView<MainViewModel> {
	@FXML
	AnchorPane mainPane;
	
	@InjectViewModel
	MainViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Method called when the File > New > Creature button is pressed.
	 */
	public void newCreatureButtonPressed() {
		mainPane.getChildren().clear();
		mainPane.getChildren().add(viewModel.newCreature());
	}

}
