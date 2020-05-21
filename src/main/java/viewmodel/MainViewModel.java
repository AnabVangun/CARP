package viewmodel;

import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Parent;
import model.creatures.Creature;
import view.creatures.CreatureView;
import viewmodel.creatures.CreatureViewModel;

public class MainViewModel implements ViewModel {
	private String creatureBundleName = "bundles.creature.creatureBundle";
	/**
	 * Initialise a new Creature and return the view used to display it.
	 * @return a view used to display a creature.
	 */
	public Parent newCreature() {
		ViewTuple<CreatureView, CreatureViewModel> creatureViewTuple =
				FluentViewLoader.fxmlView(CreatureView.class)
				.viewModel(new CreatureViewModel(new Creature()))
				.resourceBundle(ResourceBundle.getBundle(creatureBundleName))
				.load();
		return creatureViewTuple.getView();
	}
}
