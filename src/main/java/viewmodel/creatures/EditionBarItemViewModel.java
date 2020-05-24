package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;

public class EditionBarItemViewModel implements ViewModel {
	private final int localIndex;
	private ReadOnlyStringWrapper emphasisStatus = new ReadOnlyStringWrapper();
	public final String label;
	private final WeakChangeListener<Number> listener;
	/**
	 * Initialises an element to display in the EditionBar
	 * @param label of the text element
	 * @param currentPhaseIndex index of the current edition phase
	 * @param localIndex index of the local cell.
	 */
	public EditionBarItemViewModel(String label, ObservableIntegerValue currentPhaseIndex, int localIndex) {
		this.label = label;
		this.localIndex = localIndex;
		this.updateStatus(currentPhaseIndex.get());
		/*
		 * Create a listener to update the local status when the current phase
		 * index changes.
		 */
		this.listener = new WeakChangeListener<>(
			(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
			-> updateStatus((int) newValue));
		currentPhaseIndex.addListener(this.listener);
	}

	/**
	 * Refreshes the value of the local status property based on the given
	 * phase index.
	 * @param newPhaseIndex index of the current phase in the creature edition
	 * process.
	 */
	private void updateStatus(int newPhaseIndex) {
		if(this.localIndex == newPhaseIndex) {
			this.emphasisStatus.set(Status.CURRENT_PHASE.style);
		} else {
			this.emphasisStatus.set(Status.NULL.style);
		}
	}
	
	/**
	 * Define the multiple types of status of the local item relatively to the 
	 * current phase in the creature edition process.
	 * @author TLM
	 */
	public static enum Status{
		/** The local item is not special. */
		NULL(null),
		/** The local item corresponds to the current phase. */
		CURRENT_PHASE("emphasis");
		
		public final String style;
		private Status(String style) {
			this.style = style;
		}
	}
	
	/**
	 * @return the {@link Status} of the local object.
	 */
	public ReadOnlyStringProperty getStatusStyle() {
		return this.emphasisStatus.getReadOnlyProperty();
	}
}
