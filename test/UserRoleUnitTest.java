import models.UserRole;
import org.junit.Test;

import java.util.Arrays;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 User: justin.podzimek
 Date: 10/1/15
 */
public class UserRoleUnitTest {

    @Test
    public void testGetChildRole() {
        running(fakeApplication(), () -> {
            assertThat(UserRole.FA.getChildRole(false), is(UserRole.AGENT));
            assertThat(UserRole.FA.getChildRole(true), is(UserRole.EFA_ASSISTANT));
            assertThat(UserRole.AGENT.getChildRole(false), is(UserRole.PRODUCER));
        });
    }

    @Test
    public void testGetChildRoles() {
        running(fakeApplication(), () -> {
            assertThat(UserRole.FA.getChildRoles(true), is(Arrays.asList(
                    UserRole.EFA_ASSISTANT,
                    UserRole.SUB_EFA,
		            UserRole.AGENT,
		            UserRole.PRODUCER
            )));
            assertThat(UserRole.FA.getChildRoles(false), is(Arrays.asList(
                    UserRole.AGENT,
                    UserRole.PRODUCER
            )));
        });
    }

}
