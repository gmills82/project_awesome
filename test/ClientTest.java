import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.ClientCtrl;
import models.Client;
import models.Referral;
import models.UserModel;
import org.junit.After;
import org.junit.Test;
import play.mvc.*;
import play.test.FakeRequest;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 User: justin.podzimek
 Date: 8/12/15
 */
public class ClientTest {

    @After
    public void cleanUp() {
        running(fakeApplication(), () -> {
            Client client = Client.getLastInsertedClient();
            Client.removeById(client.id);
        });
    }

    /**
     Test to verify duplicate client entries are being rejected
     */
    @Test
    public void testDuplicateClientCreation() {

        running(fakeApplication(), () -> {

            Client client = new Client();
            client.name = "Test Client " + new Date().getTime();
            client.phoneNumber = "(000) 000-0000";

            FakeRequest request = new FakeRequest("POST", "/json/client");
            JsonNode node = new ObjectMapper().valueToTree(client);

            // Initial request
            request.withJsonBody(node);
            Result result = route(request);
            assertThat(status(result)).isEqualTo(CREATED);

            // Duplicate
            request.withJsonBody(node);
            Result duplicateResult = route(request);
            assertThat(status(duplicateResult)).isEqualTo(CONFLICT);
        });

    }
}
