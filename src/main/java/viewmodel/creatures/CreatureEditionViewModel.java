package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import model.creatures.Creature;
/**
 * ViewModel used to manage the edition component of a creature.
 * @author TLM
 *
 */
public class CreatureEditionViewModel implements ViewModel {
	/** ViewModel managing the bar describing the edition phases. */
	public final EditionBarViewModel phasesVM;
	public final ObservableIntegerValue currentPhaseIndex;
	private final StringBinding currentPhaseDescriptionKey;
	
	/**
	 * Initialises a {@link CreatureEditionViewModelTest} object with a populated
	 * {@link EditionBarViewModel}.
	 * @param currentPhaseIndex	index of the current phase of edition
	 */
	public CreatureEditionViewModel(ObservableIntegerValue currentPhaseIndex) {
		this.currentPhaseIndex = currentPhaseIndex;
		this.phasesVM = new EditionBarViewModel(this.currentPhaseIndex);
		this.currentPhaseDescriptionKey = Bindings.createStringBinding(
				() -> Creature.InitStatus.values()[this.currentPhaseIndex.get()].name()+"_PHASE",
				this.currentPhaseIndex);
	}

	/**
	 * Returns the key to get the description of the current phase in the 
	 * resource bundle.
	 * The key is expected to be the name of the current phase in 
	 * {@link Creature.InitStatus}.
	 * @return the key to get the description of the current edition phase.
	 */
	public ObservableValue<String> getCurrentPhaseDescriptionKey(){
		return this.currentPhaseDescriptionKey;
	}

}
