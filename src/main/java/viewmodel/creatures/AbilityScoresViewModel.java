package viewmodel.creatures;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyListWrapper;
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
	private ReadOnlyListWrapper<AbilityScoreListItemViewModel> abilityList;
	
	public AbilityScoresViewModel(AbilityScores abilities) {
		List<AbilityScoreListItemViewModel> init = new ArrayList<>();
		for(AbilityName ability : AbilityName.values()) {
			init.add(new AbilityScoreListItemViewModel(ability, 
					abilities != null ? abilities.getScore(ability) : null));
		}
		abilityList = new ReadOnlyListWrapper<>(FXCollections.observableArrayList(init));
	}
	/**
	 * @return the ability scores as a an observable list, where each 
	 * {@link service.parameters.CreatureParameters.AbilityName} is represented
	 * by a viewModel, even if it is null.
	 */
	@Override
	public ObservableList<AbilityScoreListItemViewModel> getListItems() {
		return abilityList.getReadOnlyProperty();
	}
}
