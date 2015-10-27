import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;
import play.libs.F;
import play.libs.ws.WS;
import play.test.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;

public class BrowserTest {
	@Test
	public void loginSuccess() {
		running(testServer(3333), HTMLUNIT, browser -> {
			browser.goTo("http://localhost:3333");

			//Fill in creds
			browser.$("input[name=userName]").text("test@efsmanager.com");
			browser.$("input[name=password]").text("test");

			//Submit by clicking the Login button
			FluentList<FluentWebElement> submitButton = browser.$("input[type='submit']");
			submitButton.click();

			//Assert good login directs to home page
			assertThat(browser.url()).isEqualTo("http://localhost:3333/home");
		});
	}

	@Test
	public void login() {
		running(testServer(3333), HTMLUNIT, browser -> {
			browser.goTo("http://localhost:3333");

			//Fill in creds
			browser.$("input[name=userName]").text("test@efsmanager.com");
			browser.$("input[name=password]").text("saltyDog");

			//Submit by clicking the Login button
			FluentList<FluentWebElement> submitButton = browser.$("input[type='submit']");
			submitButton.click();

			//Assert good login directs to home page
			assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
			assertThat(browser.$(".alert.alert-danger")).isNotEmpty();
		});
	}
}
