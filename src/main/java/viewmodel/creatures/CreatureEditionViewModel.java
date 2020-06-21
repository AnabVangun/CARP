package viewmodel.creatures;

import java.util.Arrays;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import model.creatures.AbilityScores;
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
	/** This index tracks the phase in the creature edition process. */
	public final ObservableIntegerValue currentPhaseIndex;
	/** 
	 * Link to the encasing {@link CreatureViewModel} to request copies of 
	 * relevant parts of the creature depending on the current edition phase.
	 */
	private final CreatureViewModel creatureVM;
	/** This index tracks the user's choice of method to edit the creature. */
	//XXX set to 2 waiting for STANDARD to be implemented
	private final IntegerProperty currentSelectedMethod = new SimpleIntegerProperty(2);
	/** This contains the key to get the String describing the current phase.*/
	private final ReadOnlyStringWrapper currentPhaseDescriptionKey = new ReadOnlyStringWrapper();
	/** 
	 * This evaluates to true when the user can choose between different 
	 * methods to handle the current phase of the edition process.
	 */
	private final ReadOnlyBooleanWrapper hasSeveralMethods = new ReadOnlyBooleanWrapper();
	private final InvalidationListener currentPhaseListener = 
			(Observable observable) -> this.phaseRefresh();
	/** 
	 * This contains the keys to get the names of the methods available to the 
	 * user in the current phase.
	 */
	private final ReadOnlyListWrapper<String> methodNameKeys = 
			new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
	/** This contains the key to get the String describing the selected method.*/
	private final ReadOnlyObjectWrapper<String> methodDescriptionKey = new ReadOnlyObjectWrapper<>();
	private final InvalidationListener currentMethodListener = 
			(Observable observable) -> this.methodRefresh();
	private ObjectBinding<String> methodDescriptionBinding;
	/** 
	 * This evaluates to true when the user is expected to do something in the
	 * current phase.
	 */
	private final ReadOnlyBooleanWrapper isActionExpected = new ReadOnlyBooleanWrapper();
	
	/**
	 * Initialises a {@link CreatureEditionViewModel} object with a populated
	 * {@link EditionBarViewModel}.
	 * @param creatureVM	reference to the {@link CreatureViewModel} 
	 * managing the creature to edit.
	 */
	public CreatureEditionViewModel(CreatureViewModel creatureVM) {
		this.creatureVM = creatureVM;
		this.currentPhaseIndex = this.creatureVM.getCurrentEditionPhase();
		this.phasesVM = new EditionBarViewModel(this.currentPhaseIndex);
		this.currentPhaseIndex.addListener(new WeakInvalidationListener(this.currentPhaseListener));
		this.currentSelectedMethod.addListener(new WeakInvalidationListener(currentMethodListener));
		this.phaseRefresh();
	}
	
	/**
	 * Refreshes all parameters that depend on the current phase index.
	 */
	private void phaseRefresh() {
		Creature.InitStatus phase = Creature.InitStatus.values()[this.currentPhaseIndex.get()];
		this.currentPhaseDescriptionKey.set(phase.name()+"_PHASE");
		this.methodRefresh(phase);
	}
	
	/**
	 * Refreshes all parameters that depend on the current method choice.
	 * @param phase	current phase of the edition process.
	 */
	private void methodRefresh(Creature.InitStatus phase) {
		switch(phase) {
		case ABILITIES:
			this.hasSeveralMethods.set(true);
			this.setMethodChoiceKey(AbilityGenerationMethod.values());
			this.isActionExpected.set(true);
			this.AbilitiesMethodRefresh(AbilityGenerationMethod.values()[this.currentSelectedMethod.get()]);
			break;
		case REVIEW:
		case COMPLETED:
			this.hasSeveralMethods.set(false);
			this.setMethodChoiceKey(null);
			this.isActionExpected.set(false);
			break;
		default:
			throw new NotYetImplementedException();
		}
	}

	/**
	 * Refreshes all parameters that depend on the current method choice.
	 */
	private void methodRefresh() {
		this.methodRefresh(Creature.InitStatus.values()[this.currentPhaseIndex.get()]);
	}
	
	/**
	 * Refreshes the view depending on the method used to generate 
	 * {@link AbilityScores}. This assumes that the current edition phase is
	 * consistent.
	 * @param method picked to generate the scores.
	 */
	private void AbilitiesMethodRefresh(AbilityGenerationMethod method) {
		switch(method) {
		case STANDARD:
		case DICE_POOL:
			//TODO test and implement
		case DIRECT_ASSIGNMENT:
			break;
		default:
			throw new NotYetImplementedException();
		}
		creatureVM.getAbilities().get().setGenerationMethod(
				AbilityGenerationMethod.values()[this.currentSelectedMethod.get()]);
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
	 * Returns the index of the selected method for the current phase. The 
	 * value has no meaning when
	 * {@link CreatureEditionViewModel#hasSeveralMethods()} evaluates to false.
	 * @return an observable integer which corresponds to the index of the 
	 * selected method in
	 * {@link CreatureEditionViewModel#getMethodNameKeys()}.
	 */
	public IntegerProperty getSelectedMethodIndex() {
		return this.currentSelectedMethod;
	}
	
	/**
	 * Returns an {@link ObservableListValue} containing the descriptions of 
	 * the methods for the current phase. The list is empty if there is only 
	 * one method.
	 * @return a list containing the keys to fetch the descriptions of the 
	 * methods for the current phase from the resource bundle.
	 */
	public ObservableValue<String> getMethodDescriptionKey(){
		return this.methodDescriptionKey.getReadOnlyProperty();
	}
	
	/**
	 * Updates the list used by the object to store the names of the methods 
	 * for the current phase and the string describing the selected one.
	 * @param values	all the values of the enum describing the methods in
	 * the current phase, null if the current phase has only one method.
	 */
	@SuppressWarnings("rawtypes")
	private void setMethodChoiceKey(Enum[] values) {
		if(values == null) {
			this.methodNameKeys.clear();
			this.methodDescriptionKey.set("");
			return;
		}
		this.methodNameKeys.setAll(Arrays.asList(values).stream()
				.map(value -> value.name() + "_BUTTON").toArray(String[]::new));
		if(this.currentSelectedMethod != null) {
			this.methodDescriptionBinding = new ObjectBinding<String>() {
				{super.bind(currentSelectedMethod);}
				@Override
				protected String computeValue() {
					return values[currentSelectedMethod.get()].name() + "_DESCRIPTION";
				}	
			};
			this.methodDescriptionKey.bind(methodDescriptionBinding);
		} else {
			this.methodDescriptionBinding = null;
			this.methodDescriptionKey.unbind();
		}
	}

	/**
	 * Returns an observable boolean indicating whether the user is expected to
	 * make a choice in the current phase given the current choice of method 
	 * (if applicable).
	 * @return	an observableValue that is true if and only if the user must 
	 * make a choice to advance in the creature edition process.
	 */
	public ObservableBooleanValue isActionExpected() {
		return this.isActionExpected.getReadOnlyProperty();
	}
}
