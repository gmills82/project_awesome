package actors;

import akka.actor.*;
import controllers.ReferralCtrl;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * User: Grant
 * Date: 6/26/15
 * Time: 1:05 PM
 */
public class DeclinedReferralActor extends UntypedActor {
	public static final String CLEAN_DECLINED_REFERRALS = "CLEAN_DECLINED_REFERRALS";
	private Scheduler scheduler = Akka.system().scheduler();
	private Cancellable cancel;
	public static Props props = Props.create(DeclinedReferralActor.class);

	@Override
	public void preStart(){
		//TODO: Determine difference in minutes between currentTime and desiredTime. Set that as the initialDelay
		Date now = new Date();
		Calendar nightCal = new GregorianCalendar();
		nightCal.set(Calendar.HOUR_OF_DAY, 23);
		nightCal.set(Calendar.MINUTE, 0);
		Date nightDate = nightCal.getTime();
		Logger.debug("Time left till we clean declined referrals: " + (nightDate.getTime() - now.getTime()));

		cancel = scheduler.schedule(
			Duration.create(nightDate.getTime() - now.getTime(), TimeUnit.MILLISECONDS),
			//Frequency is unused
			Duration.create(24, TimeUnit.HOURS),
			self(),
			CLEAN_DECLINED_REFERRALS,
			Akka.system().dispatcher(),
			null
		);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if(message.toString() == CLEAN_DECLINED_REFERRALS) {
			Logger.debug("Cleaning declined referrals");
			try {
				ReferralCtrl.cleanOpenDeclinedReferrals();
			}catch(Exception e) {
				Logger.error("Cleaning declined referrals encountered an error: " + e);
			}
		}
	}

	@Override
	public void postStop() {
		//Not sure if this is necessary
		cancel.cancel();
	}
}
