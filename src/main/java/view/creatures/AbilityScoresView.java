package view.creatures;

import java.net.URL;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import viewmodel.creatures.AbilityScoreListItemViewModel;
import viewmodel.creatures.AbilityScoresViewModel;

public class AbilityScoresView implements Initializable, FxmlView<AbilityScoresViewModel> {
	@FXML
	private ListView<AbilityScoreListItemViewModel> abilityList;
	
	@InjectViewModel
	private AbilityScoresViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		abilityList.setItems(viewModel.getAbilityList());
		ViewListCellFactory<AbilityScoreListItemViewModel> cellFactory =
				CachedViewModelCellFactory.createForFxmlView(AbilityScoreListItemView.class);
		abilityList.setCellFactory(cellFactory);
	}

}
