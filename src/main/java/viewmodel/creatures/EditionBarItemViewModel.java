package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import viewmodel.tools.ViewModelParameters;

public class EditionBarItemViewModel implements ViewModel {
	private final int localIndex;
	private ReadOnlyStringWrapper emphasisStyle = new ReadOnlyStringWrapper();
	public final String label;
	private final ChangeListener<Number> listener;
	/**
	 * Initialises an element to display in the EditionBar.
	 * @param label of the text element.
	 * @param currentPhaseIndex index of the current edition phase.
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
		this.listener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
			-> updateStatus((int) newValue);
		currentPhaseIndex.addListener(new WeakChangeListener<>(listener));
	}

	/**
	 * Refreshes the value of the local status property based on the given
	 * phase index.
	 * @param newPhaseIndex index of the current phase in the creature edition
	 * process.
	 */
	private void updateStatus(int newPhaseIndex) {
		if(this.localIndex == newPhaseIndex) {
			this.emphasisStyle.set(ViewModelParameters.Styles.STRONG_EMPHASIS.name());
		} else {
			this.emphasisStyle.set(null);
		}
	}
	
	/**
	 * @return the {@link Emphasis} of the local object.
	 */
	public ReadOnlyStringProperty getEmphasisStyle() {
		return this.emphasisStyle.getReadOnlyProperty();
	}
}
