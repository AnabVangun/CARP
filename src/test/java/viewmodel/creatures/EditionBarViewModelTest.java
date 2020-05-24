package viewmodel.creatures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import model.creatures.Creature;

/**
 * Test both {@link EditionBarViewModel} and {@link EditionBarItemViewModel}.
 * @author TLM
 *
 */
public class EditionBarViewModelTest {
	private EditionBarViewModel viewModel;
	private SimpleIntegerProperty phase = new SimpleIntegerProperty(0);

	/**
	 * Initialises an EditionBarViewModel.
	 * Checks that neither the EditionBarViewModel constructor nor the
	 * EditionBarItemViewModel one throws exceptions in a normal situation.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		viewModel = new EditionBarViewModel(phase);
	}
	
	/**
	 * Checks that the {@link EditionBarViewModel} constructor throws a NPE 
	 * when initialised on a null value.
	 */
	@Test
	public void testConstructor() {
		try {
			viewModel = new EditionBarViewModel(null);
			fail("The edition bar should always have a current phase");
		} catch (NullPointerException e) {}
	}

	/**
	 * Checks that {@link EditionBarViewModel#getListItems()} returns a list
	 * containing all the elements in {@link Creature#EDITION_STATUSES}.
	 */
	@Test
	public void testGetListItems() {
		int i = 0;
		for(Creature.InitStatus status : Creature.EDITION_STATUSES) {
			assertEquals("The EditionBarViewModel must transform each element of EDITION_STATUSES in a list item", 
					status.toString(), 
					viewModel.getListItems().get(i).label);
			i++;
		}
	}
	
	/**
	 * Checks that {@link EditionBarItemViewModel#getStatus()} returns 
	 * {@link EditionBarItemViewModel.Status#CURRENT_PHASE} if and only if the
	 * object has the same index as the phase.
	 */
	@Test
	public void testGetStatus() {
		for (int i = 0; i < Creature.EDITION_STATUSES.size(); i++) {
			phase.set(i);
			for(int j = 0; j < viewModel.getListItems().size(); j++) {
				EditionBarItemViewModel.Status expectedStatus =
						j == i ? EditionBarItemViewModel.Status.CURRENT_PHASE : EditionBarItemViewModel.Status.NULL;
				assertEquals("Only the corresponding item must and can correspond to the current phase",
						expectedStatus.style,
						viewModel.getListItems().get(j).getStatusStyle().get());
			}
		}
	}

}