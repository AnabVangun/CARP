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
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	private final ChangeListener<String> textChangedListener;
	private boolean isTextChangedListenerAdded;
	private ResourceBundle resources;
	
	public AbilityScoreListItemView() {
		this.styleListener = observable -> updateStyleClass(observable.getList());
		this.editableListener = (Observable observable) -> updateScoreField();
		this.textChangedListener = (observable, newValue, oldValue) -> this.commitValue();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		abilityLabel.setText(resources.getString(viewModel.getAbilityName()));
		/*
		 * Handle ability score and modifier: set value, set style, listen to
		 * modifications. 
		 */
		viewModel.getAbilityScore().addListener(new WeakInvalidationListener(editableListener));
		updateStyleClass(viewModel.getScoreStyleClasses());
		viewModel.getScoreStyleClasses().addListener(new WeakListChangeListener<>(styleListener));
		abilityModifierField.textProperty().bind(viewModel.getAbilityModifier());
		updateStyleClass(viewModel.getModifierStyleClasses());
		viewModel.getModifierStyleClasses().addListener(new WeakListChangeListener<>(styleListener));
		//Manage editability
		updateScoreField();
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
	private void updateScoreField() {
		//Mark value as valid to listen for future invalidation events.
		viewModel.getAbilityScore().getValue();
		if(viewModel.isScoreEditable().get()) {
			this.abilityScoreField.textProperty().unbind();
			this.abilityScoreField.setTextFormatter(viewModel.getAbilityScoreFormatter());
			this.abilityScoreField.setPromptText(resources.getString("promptInteger"));
			if(!isTextChangedListenerAdded) {
				this.abilityScoreField.textProperty().addListener(textChangedListener);
				isTextChangedListenerAdded = true;
			}
			this.abilityScoreField.setEditable(true);
		}
		if(!viewModel.isScoreEditable().get()) {
			this.abilityScoreField.setEditable(false);
			if(isTextChangedListenerAdded) {
				this.abilityScoreField.textProperty().removeListener(textChangedListener);
				isTextChangedListenerAdded = false;
			}
			this.abilityScoreField.setPromptText(null);
			this.abilityScoreField.setTextFormatter(null);
			this.abilityScoreField.textProperty().bind(viewModel.getAbilityScore());
		}
	}

	//TODO Jdoc
	public void commitValue() {
		//FIXME this method is called twice whenever text is written
//		System.out.println("New value:" + abilityScoreField.getText());
		abilityScoreField.commitValue();
		viewModel.setScore((Integer) abilityScoreField.getTextFormatter().getValue());
	}
}
