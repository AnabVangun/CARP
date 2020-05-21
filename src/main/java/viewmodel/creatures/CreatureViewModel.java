package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import model.creatures.Creature;

/**
 * View model used to display a {@link Creature} object.
 * @author TLM
 */
public class CreatureViewModel implements ViewModel{
	private ReadOnlyObjectWrapper<AbilityScoresViewModel> abilities;
	
	/**
	 * Initialises a {@link CreatureViewModel} object to display a 
	 * {@link Creature object}.
	 * @param creature object to display. Cannot be null.
	 * @throws a NullPointerException if the creature is null.
	 */
	public CreatureViewModel(Creature creature) {
		if(creature == null) {
			throw new NullPointerException("Tried to initialise a CreatureViewModel on a null creature");
		}
		this.abilities = new ReadOnlyObjectWrapper<>(new AbilityScoresViewModel(creature.getAbilityScores()));
	}
	
	/**
	 * @return a viewModel to display the ability scores of the creature.
	 */
	public ObservableObjectValue<AbilityScoresViewModel> getAbilities(){
		return this.abilities.getReadOnlyProperty();
	}
}
