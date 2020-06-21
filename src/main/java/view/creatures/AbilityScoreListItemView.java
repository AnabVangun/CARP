/**
 * 
 */
package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import viewmodel.creatures.AbilityScoreListItemViewModel;

/**
 * View used to display each ability score in an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoreListItemView implements FxmlView<AbilityScoreListItemViewModel>, Initializable {
	@FXML
	private Label abilityLabel;
	
	@FXML
	private TextField abilityScoreField;
	
	@FXML
	private Label abilityModifierField;
	
	@InjectViewModel
	private AbilityScoreListItemViewModel viewModel;
	
	/** Listener used to react to changes to style classes. */
	private final ListChangeListener<String> styleListener;
	private final InvalidationListener editableListener;
	private EventHandler<KeyEvent> specialKeyHandler;
	private ResourceBundle resources;
	
	public AbilityScoreListItemView() {
		this.styleListener = observable -> updateStyleClass(observable.getList());
		this.editableListener = (Observable observable) -> updateEditability();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		abilityLabel.setText(resources.getString(viewModel.getAbilityName()));
		/*
		 * Handle ability score and modifier: set value, set style, listen to
		 * modifications. 
		 */
//		abilityScoreField.textProperty().bind(viewModel.getAbilityScore());
		viewModel.getAbilityScore().addListener(new WeakInvalidationListener(editableListener));
		updateStyleClass(viewModel.getScoreStyleClasses());
		viewModel.getScoreStyleClasses().addListener(new WeakListChangeListener<>(styleListener));
		abilityModifierField.textProperty().bind(viewModel.getAbilityModifier());
		updateStyleClass(viewModel.getModifierStyleClasses());
		viewModel.getModifierStyleClasses().addListener(new WeakListChangeListener<>(styleListener));
		//Manage editability
		specialKeyHandler = (KeyEvent e) -> {
			switch(e.getCode()) {
			case ENTER:
			case TAB:
				abilityScoreField.commitValue();
				viewModel.setScore((Integer) abilityScoreField.getTextFormatter().getValue());
			default:
			}
		};
		updateEditability();
		viewModel.isScoreEditable().addListener(new WeakInvalidationListener(editableListener));
	}
	
	/**
	 * Updates style classes of the relevant input field.
	 * @param observable	which changed and causes the update.
	 */
	private void updateStyleClass(Observable observable) {
		if(observable == viewModel.getScoreStyleClasses()) {
			abilityScoreField.getStyleClass().setAll(viewModel.getScoreStyleClasses());
		} else if (observable == viewModel.getModifierStyleClasses()){
			abilityModifierField.getStyleClass().setAll(viewModel.getModifierStyleClasses());
		} else {
			throw new IllegalStateException(
					"Tried to set style classes of an AbilityScoreListItemView after an unrelated change");
		}
	}
	
	/**
	 * Updates the editability of the object.
	 */
	private void updateEditability() {
		//XXX this line is needed to refresh the viewModel
		viewModel.getAbilityScore().getValue();
		if(viewModel.isScoreEditable().get()) {
			this.abilityScoreField.setEditable(true);
			this.abilityScoreField.setPromptText(resources.getString("promptInteger"));
			this.abilityScoreField.textProperty().unbind();
			this.abilityScoreField.setTextFormatter(viewModel.getAbilityScoreFormatter());
			this.abilityScoreField.setOnKeyPressed(specialKeyHandler);
		}
		if(!viewModel.isScoreEditable().get()) {
			this.abilityScoreField.setOnKeyPressed(null);
			this.abilityScoreField.setEditable(false);
			this.abilityScoreField.setPromptText(null);
			this.abilityScoreField.setTextFormatter(null);
			this.abilityScoreField.textProperty().bind(viewModel.getAbilityScore());
		}
	}

}
