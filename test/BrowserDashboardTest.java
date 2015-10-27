import org.junit.Test;

import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

/**
 * @author Grant Mills
 * @since 10/27/15
 */
public class BrowserDashboardTest {
	@Test
	public void EFSDashboardElementsPresent() {
		running(testServer(3333), HTMLUNIT, browser -> {
			//Check for EFS nav
			
			//Check for stats

			//Click each stat button and check text changes

			//Check for widgets

			//If widgets are not empty
				//Try to sort, filter and paginate

				//Check for all actions in action dropdown
		});
	}

	@Test
	public void AgentDashboardElementsPresent() {
		running(testServer(3333), HTMLUNIT, browser -> {
			//Check for Agent nav

			//Check for stats

			//Click each stat button and check text changes

			//Check for widgets

			//If widgets are not empty
				//Try to sort, filter and paginate

				//Check for all actions in action dropdown
		});
	}

	@Test
	public void LSPDashboardElementsPresent() {
		running(testServer(3333), HTMLUNIT, browser -> {
			//Check for LSP nav

			//Check for stats

			//Click each stat button and check text changes

			//Check for quick links

			//Check for widgets

			//If widgets are not empty
				//Try to sort, filter and paginate

				//Check for all actions in action dropdown
		});
	}
}
