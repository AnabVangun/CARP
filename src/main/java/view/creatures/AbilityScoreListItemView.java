/**
 * 
 */
package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
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
	//TODO handle score modifiability
	//TODO handle score nullification
	@FXML
	private Label abilityLabel;
	
	@FXML
	private TextField abilityScoreField;
	
	@FXML
	private TextField abilityModifierField;
	
	@InjectViewModel
	private AbilityScoreListItemViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		abilityLabel.textProperty().bind(viewModel.getAbilityName());
		if(viewModel.isScoreNotNull().get()) {
			abilityScoreField.textProperty().bind(viewModel.getAbilityScore());
		}
		abilityModifierField.textProperty().bind(viewModel.getAbilityModifier());
	}

}
