import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;
import controllers.Application;

import org.junit.Test;

import play.mvc.Result;
import play.test.FakeRequest;
import play.twirl.api.Content;
import views.html.login;

public class SampleApplicationTest {

	@Test
	public void testCallIndex() {
		Result result = callAction(
			controllers.routes.ref.Application.login(),
			new FakeRequest(GET, "/")
		);
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("text/html");
		assertThat(charset(result)).isEqualTo("utf-8");
		assertThat(contentAsString(result)).contains("Login");
	}

//	@Test
//	public void renderTemplate() {
//		Content html = login.render("Welcome to Play!");
//		assertThat(contentType(html)).isEqualTo("text/html");
//		assertThat(contentAsString(html)).contains("Welcome to Play!");
//	}

}