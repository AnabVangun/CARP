package viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import model.creatures.Creature;
import viewmodel.creatures.CreatureViewModel;

public class MainViewModel implements ViewModel {
	/**
	 * Initialise a new Creature and return the view used to display it.
	 * @return a view used to display a creature.
	 */
	public CreatureViewModel newCreature() {
		return new CreatureViewModel(new Creature());
	}
}
