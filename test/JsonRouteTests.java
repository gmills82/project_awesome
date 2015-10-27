import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Testing json routes
 */
public class JsonRouteTests {

	private WSResponse getUrl(String path) {
		return WS.url("http://localhost:3333" + path).get().get(Long.valueOf("5000"));
	}

	private JsonNode getJsonData(String path) {
		JsonNode node = getUrl(path).asJson();
		return node.get("data");
	}

	/**
	 * Tests for 200 OK Status on endpoint
	 */
	@Test
	public void testJsonRouteStatus() {
		running(testServer(3333), new Runnable() {
			public void run() {
				//Test page status
				//ID 10 is test@efsmanager.com
				assertEquals("GET /json/user/:userId", OK, getUrl("/json/user/10").getStatus());
				//ID 5324 is Frank Sinatra client
				assertEquals("GET /json/client/:clientId", OK, getUrl("/json/client/5324").getStatus());
				assertEquals("GET /json/client/history/:clientId", OK, getUrl("/json/client/history/5324").getStatus());
				assertEquals("GET /json/client/query/:query", OK, getUrl("/json/client/query/test").getStatus());
				//ID 5184 is Frank Sinatra referral
				assertEquals("GET /json/referral/:referralId", OK, getUrl("/json/referral/5184").getStatus());
				assertEquals("GET /json/referrals/:userId", OK, getUrl("/json/referrals/10").getStatus());
				assertEquals("GET /json/referrals/upcoming/:userId", OK, getUrl("/json/referrals/upcoming/10").getStatus());
				assertEquals("GET /json/appts/upcoming/:userId", OK, getUrl("/json/appts/upcoming/10").getStatus());
				assertEquals("GET /json/referrals/creator/:userId", OK, getUrl("/json/referrals/creator/10").getStatus());
				assertEquals("GET /json/referrals/team/:userId", OK, getUrl("/json/referrals/team/10").getStatus());
				assertEquals("GET /json/referrals/processing/:userId", OK, getUrl("/json/referrals/processing/10").getStatus());
				assertEquals("GET /json/assetTypes", OK, getUrl("/json/assetTypes").getStatus());
				assertEquals("GET /json/debtTypes", OK, getUrl("/json/debtTypes").getStatus());
				//ID 401 is testagent@efsmanager.com
				assertEquals("GET /agent/:agentId/referrals", OK, getUrl("/agent/401/referrals").getStatus());
				assertEquals("GET /agent/:agentId/team/referrals", OK, getUrl("/agent/401/team/referrals").getStatus());
				assertEquals("GET /stats/efs/:efsid/:from/:to", OK, getUrl("/stats/efs/10/1420494290844/1445873007394").getStatus());
				assertEquals("GET /stats/agent/:agentid/:from/:to", OK, getUrl("/stats/agent/401/1420494290844/1445873007394").getStatus());
				//ID 402 is testlsp@efsmanager.com
				assertEquals("GET /stats/producer/:producerid/:from/:to", OK, getUrl("/stats/producer/402/1420494290844/1445873007394").getStatus());

			}
		});
	}

	/**
	 * Tests for a non null jsonNode in the data part of the response body
	 */
	@Test
	public void testJsonRouteBody() {
		running(testServer(3333), new Runnable() {
			public void run() {
				//Test for non empty body
				assertNotNull("GET /json/assetTypes", getJsonData("/json/assetTypes"));
				//ID 10 is test@efsmanager.com
				assertNotNull("GET /json/user/:userId", getJsonData("/json/user/10"));
				//ID 5324 is Frank Sinatra client
				assertNotNull("GET /json/client/:clientId", getJsonData("/json/client/5324"));
				assertNotNull("GET /json/client/history/:clientId", getJsonData("/json/client/history/5324"));
				assertNotNull("GET /json/client/query/:query", getJsonData("/json/client/query/test"));
				//ID 5184 is Frank Sinatra referral
				assertNotNull("GET /json/referral/:referralId", getJsonData("/json/referral/5184"));
				assertNotNull("GET /json/referrals/:userId", getJsonData("/json/referrals/10"));
				assertNotNull("GET /json/referrals/upcoming/:userId", getJsonData("/json/referrals/upcoming/10"));
				assertNotNull("GET /json/appts/upcoming/:userId", getJsonData("/json/appts/upcoming/10"));
				assertNotNull("GET /json/referrals/creator/:userId", getJsonData("/json/referrals/creator/10"));
				assertNotNull("GET /json/referrals/team/:userId", getJsonData("/json/referrals/team/10"));
				assertNotNull("GET /json/referrals/processing/:userId", getJsonData("/json/referrals/processing/10"));
				assertNotNull("GET /json/assetTypes", getJsonData("/json/assetTypes"));
				assertNotNull("GET /json/debtTypes", getJsonData("/json/debtTypes"));
				//ID 401 is testagent@efsmanager.com
				assertNotNull("GET /agent/:agentId/team/referrals", getJsonData("/agent/401/team/referrals"));
				assertNotNull("GET /stats/efs/:efsid/:from/:to", getJsonData("/stats/efs/10/1420494290844/1445873007394"));
				assertNotNull("GET /stats/agent/:agentid/:from/:to", getJsonData("/stats/agent/401/1420494290844/1445873007394"));
				//ID 402 is testlsp@efsmanager.com
				assertNotNull("GET /stats/producer/:producerid/:from/:to", getJsonData("/stats/producer/402/1420494290844/1445873007394"));

			}
		});
	}

	/**
	 * Tests that an excel spreadsheet download is being returned
	 */
	@Test
	public void testJsonExcelRoute() {
		running(testServer(3333), new Runnable() {
			public void run() {
				WSResponse response = getUrl("/agent/401/referrals");
				Map<String, List<String>> headers = response.getAllHeaders();
				List<String> contentTypeStringArray = new ArrayList<String>();
				contentTypeStringArray.add("application/vnd.ms-excel");

				assertTrue(headers.containsKey("Content-Type"));
				assertEquals(contentTypeStringArray, headers.get("Content-Type"));

			}
		});
	}
}
