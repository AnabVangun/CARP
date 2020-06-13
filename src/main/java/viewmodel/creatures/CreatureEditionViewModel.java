package viewmodel.creatures;

import java.util.Arrays;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import model.creatures.Creature;
import model.creatures.CreatureParameters.AbilityGenerationMethod;
import service.exceptions.NotYetImplementedException;
/**
 * ViewModel used to manage the edition component of a creature.
 * @author TLM
 *
 */
public class CreatureEditionViewModel implements ViewModel {
	/** ViewModel managing the bar describing the edition phases. */
	public final EditionBarViewModel phasesVM;
	public final ObservableIntegerValue currentPhaseIndex;
	private final ReadOnlyStringWrapper currentPhaseDescriptionKey = new ReadOnlyStringWrapper();
	private final ReadOnlyBooleanWrapper hasSeveralMethods = new ReadOnlyBooleanWrapper();
	private final ChangeListener<Number> currentPhaseListener;
	private final ReadOnlyListWrapper<String> methodNameKeys = 
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	private final ReadOnlyListWrapper<String> methodDescriptionKeys = 
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	
	/**
	 * Initialises a {@link CreatureEditionViewModelTest} object with a populated
	 * {@link EditionBarViewModel}.
	 * @param currentPhaseIndex	index of the current phase of edition
	 */
	public CreatureEditionViewModel(ObservableIntegerValue currentPhaseIndex) {
		this.currentPhaseIndex = currentPhaseIndex;
		this.phasesVM = new EditionBarViewModel(this.currentPhaseIndex);
		this.currentPhaseListener = 
				(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
				-> this.refresh();
		this.currentPhaseIndex.addListener(new WeakChangeListener<>(this.currentPhaseListener));
		this.refresh();
	}
	
	/**
	 * Refreshes all parameters that depend on the current phase index.
	 */
	private void refresh() {
		Creature.InitStatus status = Creature.InitStatus.values()[this.currentPhaseIndex.get()];
		this.currentPhaseDescriptionKey.set(status.name()+"_PHASE");
		switch(status) {
		case ABILITIES:
			this.hasSeveralMethods.set(true);
			this.setMethodChoiceKey(AbilityGenerationMethod.values());
			break;
		case REVIEW:
		case COMPLETED:
			this.hasSeveralMethods.set(false);
			this.setMethodChoiceKey(null);
			break;
		default:
			throw new NotYetImplementedException();
		}
	}

	/**
	 * Returns the key to get the description of the current phase in the 
	 * resource bundle.
	 * The key is expected to be the name of the current phase in 
	 * {@link Creature.InitStatus}.
	 * @return the key to get the description of the current edition phase.
	 */
	public ObservableValue<String> getCurrentPhaseDescriptionKey(){
		return this.currentPhaseDescriptionKey.getReadOnlyProperty();
	}
	
	/**
	 * Returns an {@link ObservableBooleanValue} evaluating to true if the 
	 * current phase offers several methods to proceed.
	 * @return an observable
	 */
	public ObservableBooleanValue hasSeveralMethods() {
		return this.hasSeveralMethods.getReadOnlyProperty();
	}
	
	/**
	 * Returns an {@link ObservableListValue} containing the names of the 
	 * methods for the current phase. The list is empty if there is only one 
	 * method.
	 * @return a list containing the keys to fetch the names of the methods for
	 * the current phase from the resource bundle.
	 */
	public ObservableListValue<String> getMethodNameKeys(){
		return this.methodNameKeys.getReadOnlyProperty();
	}
	
	/**
	 * Returns an {@link ObservableListValue} containing the descriptions of 
	 * the methods for the current phase. The list is empty if there is only 
	 * one method.
	 * @return a list containing the keys to fetch the descriptions of the 
	 * methods for the current phase from the resource bundle.
	 */
	public ObservableListValue<String> getMethodDescriptionKeys(){
		return this.methodDescriptionKeys.getReadOnlyProperty();
	}
	
	/**
	 * Updates the lists used by the object to store the names and descriptions
	 * of the methods for the current phase.
	 * @param values	all the values of the enum describing the methods in
	 * the current phase, null if the current phase has only one method.
	 */
	@SuppressWarnings("rawtypes")
	private void setMethodChoiceKey(Enum[] values) {
		if(values == null) {
			this.methodNameKeys.clear();
			this.methodDescriptionKeys.clear();
			return;
		}
		this.methodNameKeys.setAll(Arrays.asList(values).stream()
				.map(value -> value.name() + "_BUTTON").toArray(String[]::new));
		this.methodDescriptionKeys.setAll(Arrays.asList(values).stream()
				.map(value -> value.name() + "_DESCRIPTION").toArray(String[]::new));
	}

}
