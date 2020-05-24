package viewmodel.creatures;

import de.saxsys.mvvmfx.ViewModel;

public class EditionBarItemViewModel implements ViewModel {
	
	public final String label;
	/**
	 * Initialises an element to display in the EditionBar
	 * @param label
	 */
	public EditionBarItemViewModel(String label) {
		this.label = label;
	}

}
