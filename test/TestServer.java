import org.junit.Test;
import play.libs.ws.WS;
import play.test.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;

public class TestServer {

	/**
	 * Useful for REST Endpoint tests
	 */

	@Test
	public void testInServer() {
		running(testServer(3333), new Runnable() {
			public void run() {
				assertThat(
					WS.url("http://localhost:3333/json/user/1").get().get(Long.valueOf("5000")).getBody()
				).isNotEmpty();
			}
		});
	}
}
