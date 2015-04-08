package controllers;

import models.*;
import models.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:22 AM
 */
public class UserCtrl extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getActions(Long userId, String category) {
        ObjectNode result = Json.newObject();
		//Cache responses for an hour
		response().setHeader("Cache-Control", "max-age=3600");
        result.put("status", "OK");

        UserModel currentUser = UserModel.getById(userId);
        List<Action> actionList = Action.actionsByUserRole(currentUser.roleType.getPermissionLevel());
        if(null != actionList) {
            actionList = Action.filterByCategory(actionList, category);
        }
        //Convert actionList to an action json array
        JsonNode actions = Json.toJson(actionList);
        result.put("data", actions);

        return ok(result);
    }

	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateUser() {
		Logger.debug(request().body().asJson().toString());
		Form<UserModel> userForm = Form.form(UserModel.class).bindFromRequest();

		//There is no error on the form so it is now safe to get the User
		UserModel user = userForm.get();
		user.update();

		response().setHeader(LOCATION, routes.UserCtrl.getUser(user.id).url());

		return ok();
	}
	

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getUser(Long userId) {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");

		UserModel currentUser = UserModel.getById(userId);
		if(null == currentUser) {
			return badRequest("User not found");
		}
		JsonNode user = Json.toJson(currentUser);
		result.put("data", user);
		
		return ok(result);
	}


	/**
	 * Aggregate user's percentages and statistics
	 * @return
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getUserStats(Long userId) {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");

		UserModel currentUser = UserModel.getById(userId);
		if(null == currentUser) {
			return badRequest("User not found");
		}

		//Stats object
		ObjectNode stats = Json.newObject();

		//Get all closed referrals in time range and check percent wasProductive
		Date date = new Date();
		date.setMonth(new Date().getMonth() - 1);
		Long timeRange = date.getTime();

		List<Referral> referralsCompletedInTimeRange = Referral.getByAssignedIdInRange(userId, timeRange);
		Float productiveCount = 0.00f;
		for (Referral aReferralsCompletedInTimeRange : referralsCompletedInTimeRange) {
			if (aReferralsCompletedInTimeRange.wasProductive) {
				productiveCount++;
			}
		}

		Float percProductiveReferrals = referralsCompletedInTimeRange.size() / productiveCount;
		stats.set("percProductiveReferrals", Json.toJson(percProductiveReferrals));

		return ok(result);
	}
}
