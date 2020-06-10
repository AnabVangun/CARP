/**
 * 
 */
package view.creatures;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
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
	private TextField abilityModifierField;
	
	@InjectViewModel
	private AbilityScoreListItemViewModel viewModel;
	
	/** Listener used to react to changes to style classes. */
	private final ListChangeListener<String> listener;
	
	public AbilityScoreListItemView() {
		// Prepare a listener to update style classes
		this.listener = new ListChangeListener<String>() {
			@Override
		     public void onChanged(Change<? extends String> c) {
				TextField toUpdate;
				if(c.getList() == viewModel.getModifierStyleClasses()) {
					toUpdate = abilityModifierField;
				} else {
					toUpdate = abilityScoreField;
				}
				while (c.next()) {
					if (c.wasPermutated()) {
						//Do nothing, the change has no effect
					} else if (c.wasUpdated()) {
						//Do nothing, the strings cannot be updated
					} else {
						//The modification is an addition and/or a deletion
						setStyleClass(toUpdate, c.getAddedSubList(), c.getRemoved());
					}
				}
			}
		};
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		abilityLabel.setText(resources.getString(viewModel.getAbilityName()));
		/*
		 * Handle ability score and modifier: set value, set style, listen to
		 * modifications. 
		 */
		abilityScoreField.textProperty().bind(viewModel.getAbilityScore());
		setStyleClass(abilityScoreField, viewModel.getScoreStyleClasses(), null);
		viewModel.getScoreStyleClasses().addListener(new WeakListChangeListener<>(listener));
		abilityModifierField.textProperty().bind(viewModel.getAbilityModifier());
		setStyleClass(abilityModifierField, viewModel.getModifierStyleClasses(), null);
		viewModel.getModifierStyleClasses().addListener(new WeakListChangeListener<>(listener));
	}
	
	/**
	 * Removes and adds relevant style classes to the input field.
	 * @param field		Text field to update.
	 * @param added		Style classes to add to the text field.
	 * @param removed	Style classes to remove from the text field.
	 */
	private static void setStyleClass(TextField field, 
			List<? extends String> added, 
			List<? extends String> removed) {
		if(removed != null) {
			field.getStyleClass().removeAll(removed);
		}
		if(added != null) {
			field.getStyleClass().addAll(added);
		}
	}

}
