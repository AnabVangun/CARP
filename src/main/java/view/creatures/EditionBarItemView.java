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
import viewmodel.creatures.EditionBarItemViewModel;

public class EditionBarItemView
	implements Initializable, FxmlView<EditionBarItemViewModel> {
	
	private final ChangeListener<String> listener;
	
	public EditionBarItemView() {
		this.listener = (ObservableValue<? extends String> observable, String oldValue, String newValue) 
			-> setStyleClass(newValue);
	}
	
	@InjectViewModel
	private EditionBarItemViewModel viewModel;
	
	@FXML
	private Label label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		label.setText(resources.getString(viewModel.label));
		//Bind style to viewmodel style property
		setStyleClass(viewModel.getEmphasisStyle().get());
		viewModel.getEmphasisStyle().addListener(new WeakChangeListener<>(this.listener));
	}
	
	/**
	 * Sets the style class of the item.
	 * @param styleClass to apply to the node.
	 */
	private void setStyleClass(String styleClass) {
		label.getStyleClass().setAll(styleClass);
	}

}
