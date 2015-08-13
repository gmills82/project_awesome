import models.Referral;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 User: justin.podzimek
 Date: 7/28/15
 */
public class ReferralTest {

    @Test
    public void testDateCreation() {

        running(fakeApplication(), () -> {
            Referral referral = new Referral();

            // Simple date conversion
            referral.setNextStepDate("2015-07-28 01:30 AM");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 1, 30).getMillis()));

            // Using PM (01:30 === 13:30)
            referral.setNextStepDate("2015-07-28 01:30 PM");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 13, 30).getMillis()));

            // No half-day defined.
            referral.setNextStepDate("2015-07-28 01:30");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 13, 30).getMillis()));

            // Extra characters in the string, no time defined
            referral.setNextStepDate("2015-07-28 undefined");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 12, 0).getMillis()));

            // Missing minutes in time field
            referral.setNextStepDate("2015-07-28 01");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 13, 0).getMillis()));

            // Minority format
            referral.setNextStepDate("07/28/15");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 12, 0).getMillis()));

            // Minority format with full year
            referral.setNextStepDate("07/28/2015");
            assertThat(referral.getNextStepTimestamp().getTime(), is(new DateTime(2015, 7, 28, 12, 0).getMillis()));

        });

    }
}
