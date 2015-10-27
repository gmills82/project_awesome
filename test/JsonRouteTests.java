import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;

public class JsonRouteTests {

	/**
	 * Useful for testing http routes and live page status
	 */

	private WSResponse getUrl(String path) {
		return WS.url("http://localhost:3333" + path).get().get(Long.valueOf("5000"));
	}

	private Object getJsonData(String path) {
		Map<String, Object> dataMap = Collections.EMPTY_MAP;
		ObjectMapper mapper = new ObjectMapper();
		Object data = null;
		try {
			dataMap = mapper.readValue(getUrl(path).getBody(), HashMap.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(dataMap != Collections.EMPTY_MAP) {
			data = dataMap.get("data");
		}

		return data;
	}

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

	@Test
	public void testJsonRouteBody() {
		running(testServer(3333), new Runnable() {
			public void run() {
				//Test for non empty body
				assertNotNull("GET /json/assetTypes", getJsonData("/json/assetTypes"));
			}
		});
	}
}
