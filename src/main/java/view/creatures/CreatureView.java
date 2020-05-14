package view.creatures;

import viewmodel.creatures.CreatureViewModel;

import java.util.ArrayList;
import java.util.List;

import service.parameters.CreatureParameters.AbilityName;

/**
 * Mock view to try out MVVM.
 * @author TLM
 *
 */
public class CreatureView {
	CreatureViewModel viewModel;

	public CreatureView() {
		this.viewModel = new CreatureViewModel();
		int[] lengths = new int[3];
		List<String[]> lines = new ArrayList<String[]>();
		for(AbilityName name: AbilityName.values()) {
			String[] line = new String[] {name.toString(), viewModel.getReadOnlyScores().get(name), 
					viewModel.getReadOnlyModifiers().get(name)};
			for (int i = 0; i < lengths.length; i++) {
				lengths[i] = lengths[i] > line[i].length() ? lengths[i] : line[i].length();
			}
			lines.add(line);
		}
		StringBuilder builder = new StringBuilder(500);
		builder.append("Creature:\n");
		builder.append(String.format("+%" + lengths[0] + "s", ""));
		builder.append(String.format("+%" + lengths[1] + "s", ""));
		builder.append(String.format("+%" + lengths[2] + "s+\n", ""));
		for(String[] line: lines) {
			builder.append("|");
			for(int i = 0; i < lengths.length; i++) {
				builder.append(String.format("%-" + lengths[i] + "s|", line[i]));
			}
			builder.append("\n");
		}
		System.out.println(builder.toString());
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CreatureView();
	}

}
