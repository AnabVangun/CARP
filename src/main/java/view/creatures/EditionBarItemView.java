package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import viewmodel.creatures.EditionBarItemViewModel;

public class EditionBarItemView extends ListCell<String> 
	implements Initializable, FxmlView<EditionBarItemViewModel> {
	
	@InjectViewModel
	EditionBarItemViewModel viewModel;
	
	@FXML
	Label label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		label.setText(resources.getString(viewModel.label));
	}

}
