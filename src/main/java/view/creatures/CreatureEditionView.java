package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
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
	@FXML
	HBox methodChoiceContainer;
	@FXML
	ListView<String> methodChoice;
	@FXML
	Text methodDescription;
	@InjectViewModel
	CreatureEditionViewModel vm;
	ChangeListener<String> phaseListener;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Set edition bar to display the phases
		ViewTools.setUpListView(phaseBar, 
				new EditionBarViewModel(vm.currentPhaseIndex).getListItems(), 
				EditionBarItemView.class, resources);
		//Set elements related to phase
		refreshPhase(resources);
		phaseListener = (ObservableValue<? extends String> observable, String oldValue, String newValue)
				-> refreshPhase(resources);
		vm.getCurrentPhaseDescriptionKey().addListener(new WeakChangeListener<String>(phaseListener));
		//Set elements related to method description
		methodDescription.textProperty().bind(Bindings.createStringBinding(
				() -> resources.getString(vm.getMethodDescriptionKeys()
						.get(methodChoice.getFocusModel().focusedIndexProperty().get())), 
				vm.getMethodDescriptionKeys(),
				methodChoice.getFocusModel().focusedIndexProperty()));
	}

	/**
	 * Refreshes all parts of the elements based on the current phase.
	 * @param resources	from which to get internationalised text.
	 */
	private void refreshPhase(ResourceBundle resources) {
		//Set the description of the current phase
		phaseDescription.setText(resources.getString(vm.getCurrentPhaseDescriptionKey().getValue()));
		//Set the method choice, visible only if several methods exist
		methodChoiceContainer.setManaged(vm.hasSeveralMethods().get());
		methodChoiceContainer.setVisible(vm.hasSeveralMethods().get());
		ViewTools.setI18nListItems(methodChoice, vm.getMethodNameKeys(), resources);
		methodChoice.getSelectionModel().clearAndSelect(2);//XXX 2 temporary waiting for STANDARD to be implemented
	}

}
