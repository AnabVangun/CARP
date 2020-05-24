package viewmodel.creatures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.creatures.AbilityScores;
import service.parameters.CreatureParameters.AbilityName;

/**
 * ViewModel used to display an 
 * {@link model.creatures.AbilityScores} object.
 * @author TLM
 */
public class AbilityScoresViewModel implements SimpleListViewModel<AbilityScoreListItemViewModel> {
	public AbilityScoresViewModel(AbilityScores abilities) {
		for(AbilityName ability : AbilityName.values()) {
			abilityList.add(new AbilityScoreListItemViewModel(ability, 
					abilities != null ? abilities.getScore(ability) : null));
		}
	}
	
	private ObservableList<AbilityScoreListItemViewModel> abilityList = FXCollections.observableArrayList();
	
	/**
	 * @return the ability scores as a an observable list, where each 
	 * {@link service.parameters.CreatureParameters.AbilityName} is represented
	 * by a viewModel, even if it is null.
	 */
	@Override
	public ObservableList<AbilityScoreListItemViewModel> getListItems() {
		return abilityList;
	}
}
