package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import view.creatures.CreatureView.CreaturePart;
import view.tools.ViewTools;
import viewmodel.creatures.CreatureEditionViewModel;
import viewmodel.creatures.EditionBarItemViewModel;
import viewmodel.creatures.EditionBarViewModel;

public class CreatureEditionView implements Initializable, FxmlView<CreatureEditionViewModel> {
	@FXML
	private VBox root;
	@FXML
	private ListView<EditionBarItemViewModel> phaseBar;
	@FXML
	private Text phaseDescription;
	@FXML
	private HBox methodChoiceContainer;
	@FXML
	private ListView<String> methodChoice;
	@FXML
	private Text methodDescription;
	@FXML
	private HBox methodActionContainer;
	@FXML
	private AnchorPane creaturePartCopy;
	@InjectViewModel
	private CreatureEditionViewModel vm;
	/** Listener reacting to changes in the current phase. */
	private InvalidationListener phaseListener;
	/** Listener reacting to changes in the current selected method. */
	private ChangeListener<Number> methodListener;
	/** View of the creature being edited to get copies of selected parts. */
	private final CreatureView creatureView;
	
	/**
	 * Initialises a {@link CreatureEditionView} to handle the edition of a 
	 * creature.
	 * @param creatureView displaying the creature to edit.
	 */
	protected CreatureEditionView(CreatureView creatureView) {
		this.creatureView = creatureView;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Set edition bar to display the phases
		ViewTools.setUpListView(phaseBar, 
				new EditionBarViewModel(vm.currentPhaseIndex).getListItems(), 
				EditionBarItemView.class, resources);
		//Set elements related to phase
		refreshPhase(resources);
		phaseListener = (Observable observable) -> refreshPhase(resources);
		vm.getCurrentPhaseDescriptionKey().addListener(new WeakInvalidationListener(phaseListener));
		//Set elements related to method description
		methodListener = (observable, oldValue, newValue) -> refreshMethod(resources);
		methodChoice.getSelectionModel().selectedIndexProperty().addListener(
				new WeakChangeListener<>(methodListener));
		refreshMethod(resources);
		methodDescription.textProperty().bind(
				ViewTools.I18nStringBinding(resources, vm.getMethodDescriptionKey()));
		vm.selectedMethodIndexProperty().bind(methodChoice.getSelectionModel().selectedIndexProperty());
	}

	/**
	 * Refreshes all parts of the view based on the current phase.
	 * @param resources	from which to get internationalised text.
	 */
	private void refreshPhase(ResourceBundle resources) {
		//Set the description of the current phase
		phaseDescription.setText(resources.getString(vm.getCurrentPhaseDescriptionKey().getValue()));
		/*
		 * Make the method choice visible only if several methods exist. This
		 * is done here rather than by binding because the visibility can only
		 * change when changing phase, not while in the middle of a phase.
		 */
		methodChoiceContainer.setManaged(vm.hasSeveralMethods().get());
		methodChoiceContainer.setVisible(vm.hasSeveralMethods().get());
		ViewTools.setI18nListItems(methodChoice, vm.getMethodNameKeys(), resources);
		//Set the copy of the CreatureView being edited
		switch(vm.currentPhaseIndex.get()) {
		case 0:
			creaturePartCopy.getChildren().setAll(creatureView.getCreaturePart(CreaturePart.ABILITIES));
			break;
		default:
			creaturePartCopy.getChildren().clear();
		}
	}
	
	/**
	 * Refreshes all parts of the view based on the current selected method.
	 * @param resources	from which to get internationalised text.
	 */
	private void refreshMethod(ResourceBundle resources) {
		methodActionContainer.setManaged(vm.isActionExpected().get());
		methodActionContainer.setVisible(vm.isActionExpected().get());
	}

}
