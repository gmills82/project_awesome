package controllers;

import models.Referral;
import models.UserModel;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import utils.DateUtilities;
import views.html.reports.dailyReferrals;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: grant.mills
 * Date: 5/13/15
 * Time: 10:00 AM
 */
public class Reports extends Controller {

	public static final String CLIENT_NAME = "clientName";
	public static final String CREATOR_NAME = "creatorName";

	/**
	 * Daily referral page
	 * @return
	 */

	public static Result dailyReferrals(Long date) {
		UserModel currentUser = Application.getCurrentUser();
		if(null != currentUser) {
			Calendar todaysDate = new GregorianCalendar();
			//If we set date to 0 it means use todays date
			if(date != 0) {
				Date chosenDate = new Date(date);
				todaysDate.setTime(chosenDate);
			}
			todaysDate.set(Calendar.HOUR_OF_DAY, 0);
			todaysDate.set(Calendar.MINUTE, 0);
			todaysDate.set(Calendar.SECOND, 0);
			todaysDate.set(Calendar.MILLISECOND, 0);

			String formatedDateString = DateUtilities.getDateFormat().format(todaysDate.getTime());
			List<Map<String, String>> referralList = getDailyReferrals(todaysDate, currentUser);

			return ok(dailyReferrals.render(currentUser, formatedDateString, referralList));
		}
		return redirect(routes.Application.login());
	}

	private static List<Map<String, String>> getDailyReferrals(Calendar chosenDate, UserModel currentUser) {
		//Get all referrals in a given time
		List<Referral> todaysReferrals = Referral.getByDate(chosenDate.getTime());

		//Limit to your team's Referrals
		Set<UserModel> team = UserModel.getChildUserModelsByParentAllLevels(currentUser);
		team.add(currentUser);

		List<Long> teamMemberIds = new ArrayList<Long>();
		Iterator<UserModel> setIter = team.iterator();
		while(setIter.hasNext()) {
			UserModel teamMember = setIter.next();
			teamMemberIds.add(teamMember.id);
		}

		//Filter
//		todaysReferrals = filter(having(on(Referral.class).creatorId, isIn(teamMemberIds)), todaysReferrals);

        List<Referral> referrals = todaysReferrals.stream().filter(referral -> teamMemberIds.indexOf(referral.creatorId) != -1).collect(Collectors.toList());

        //TODO: Hitting database for each referral to get user name.
		//TODO: Make method to get all based on a group of user ids
		List<Map<String, String>> referralList = new ArrayList<Map<String, String>>();
		Iterator<Referral> iter = referrals.iterator();
		while(iter.hasNext()) {
			Referral ref = iter.next();
			Map propMap = new HashMap<String, String>();
			UserModel creator = UserModel.getById(ref.creatorId);

			propMap.put(CLIENT_NAME, ref.clientName);
			propMap.put(CREATOR_NAME, creator.firstName + " " + creator.lastName);

			referralList.add(propMap);
		}
		return referralList;
	}
}
