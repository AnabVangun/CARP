package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import view.tools.ViewTools;
import viewmodel.creatures.CreatureEditionViewModel;
import viewmodel.creatures.EditionBarItemViewModel;
import viewmodel.creatures.EditionBarViewModel;

public class CreatureEditionView implements Initializable, FxmlView<CreatureEditionViewModel> {
	@FXML
	VBox root;
	
	@FXML
	ListView<EditionBarItemViewModel> phaseBar;
	
	@FXML
	Text phaseDescription;
	
	@InjectViewModel
	CreatureEditionViewModel vm;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ViewTools.setUpListView(phaseBar, 
				new EditionBarViewModel(vm.currentPhaseIndex).getListItems(), 
				EditionBarItemView.class, resources);
		phaseDescription.textProperty().bind(Bindings.createStringBinding(
				() -> resources.getString(vm.getCurrentPhaseDescriptionKey().getValue()), 
				vm.getCurrentPhaseDescriptionKey()));
	}

}
