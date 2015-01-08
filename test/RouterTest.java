import org.junit.Test;
import play.libs.ws.WS;
import play.mvc.Result;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;


public class RouterTest {

	@Test
	public void testBadRoute() {
		running(testServer(3333), new Runnable() {
			public void run() {
				assertThat(
					WS.url("http://localhost:3333/xx/Kiki").get().get(Long.valueOf("5000")).getBody()
				).contains("404 Stormtrooper");
			}
		});
	}
}
