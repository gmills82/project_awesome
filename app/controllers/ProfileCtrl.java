package controllers;

import play.api.libs.ws.ssl.SystemConfiguration;
import play.mvc.Controller;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * User: grant.mills
 * Date: 8/21/14
 * Time: 2:13 PM
 */
public class ProfileCtrl extends Controller {
    @BodyParser.Of(BodyParser.Json.class)
    public static Result addProfile() {
        Form<Profile> profileForm = Form.form(Profile.class);
        Profile profile = profileForm.bindFromRequest().get();
        profile.adjustedRiskTolerance = profile.riskTolerance + profile.riskToleranceModifier;
        
        profile.save();
        flash().put("success", "Your client profile was created successfully");
        Logger.debug("Profile persisted with id: " + profile.id);
        response().setHeader(LOCATION, routes.ProfileCtrl.getProfile(profile.id).url());

        return status(201, "Created");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProfile(Long id) {
        ObjectNode node = Json.newObject();

        Profile profile = Profile.getById(id);
        JsonNode profileNode = Json.toJson(profile);

        node.put("data", profileNode);

        return ok(node);
    }

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getRecentProfiles(Long userId) {
		ObjectNode node = Json.newObject();

		List<Profile> profileList = Profile.getByAgentId(userId);

		//Filter list on date
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		int numberOfDaysBack = 5;
		Date timeLimit = new Date(System.currentTimeMillis() - (numberOfDaysBack * DAY_IN_MS));
		Logger.debug("Profile list original size: " + profileList.size());
		if(profileList.size() > 0) {
			profileList = filter(having(on(Profile.class).createdDate, greaterThanOrEqualTo(timeLimit.getTime())), profileList);
		}
		Logger.debug("Profile list size after filtering: " + profileList.size());

		JsonNode profileNode = Json.toJson(profileList);

		//Get each associated data object per profile
		for(Iterator<JsonNode> iter = profileNode.iterator(); iter.hasNext();) {
			ObjectNode profile = (ObjectNode) iter.next();

			//Create a client JsonNode and attach it to each profileNode
			Client clientModel = Client.getById(profile.get("clientId").longValue());
			JsonNode clientNode = Json.toJson(clientModel);

			//Attach client
			profile.set("client", clientNode);

			//Gather associated referrals that generated the profile
			Referral refModel = Referral.getById(profile.get("refId").longValue());

			//If the referral that generated this profile has been deleted then it will return null
			if(null != refModel) {
				ObjectNode refNode = (ObjectNode) Json.toJson(refModel);

				//Gather referral creator object
				//TODO: On profile create should we have referral pass its creatorId to profile?
				//TODO: Or should I make it so you cannot delete referrals that have been turned into profiles?
				UserModel refCreator = UserModel.getById(refNode.get("creatorId").longValue());
				JsonNode refCreatorNode = Json.toJson(refCreator);

				//Attach creator and referral
				refNode.set("creator", refCreatorNode);
				profile.set("referral", refNode);
			}
		}

		node.put("data", profileNode);
		return ok(node);
	}
}
