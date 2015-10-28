import com.google.common.base.Stopwatch;
import models.UserModel;
import models.UserRole;
import org.junit.Test;
import play.Logger;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static org.junit.Assert.assertTrue;

/**
 User: justin.podzimek
 Date: 10/1/15
 */
public class UserModelUnitTest {

    @Test
    public void testGetChildUserModelsByParentAllLevels() {
        running(fakeApplication(), () -> {

            UserModel user = UserModel.getByUserRole(UserRole.FA).get(1);

            final Stopwatch stopwatch = Stopwatch.createStarted();

            Set<UserModel> newChildren = UserModel.getChildUserModelsByParentAllLevels(user);

            Logger.info("Looking up children by group took: {}", stopwatch);
            stopwatch.reset();
            stopwatch.start();

            user.setGroupId(null);
            user.setRoleType(null);

            Set<UserModel> oldChildren = UserModel.getChildUserModelsByParentAllLevels(user);

            Logger.info("Looking up children iteratively took: {}", stopwatch);
            stopwatch.stop();

	        //Old method and new method are not equal and old method behavior is now deprecated. Lets test for the
	        //new method containing all the old method.
	        assertTrue("New children contains all old children", newChildren.containsAll(oldChildren));

        });
    }
}
