package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import viewmodel.creatures.EditionBarItemViewModel;

public class EditionBarItemView extends ListCell<String> 
	implements Initializable, FxmlView<EditionBarItemViewModel> {
	
	private final ChangeListener<String> listener;
	
	public EditionBarItemView() {
		super();
		/*
		 * Create a listener to update the local status when the current phase
		 * index changes.
		 */
		this.listener = (ObservableValue<? extends String> observable, String oldValue, String newValue) 
			-> setStyleClass(newValue);
	}
	
	@InjectViewModel
	EditionBarItemViewModel viewModel;
	
	@FXML
	Label label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		label.setText(resources.getString(viewModel.label));
		//Bind style to viewmodel style property
		setStyleClass(viewModel.getStatusStyle().get());
		viewModel.getStatusStyle().addListener(new WeakChangeListener<>(this.listener));
	}
	
	/**
	 * Sets the style class of the item.
	 * @param styleClass to apply to the node.
	 */
	private void setStyleClass(String styleClass) {
		label.getStyleClass().set(0, styleClass);
	}

}
