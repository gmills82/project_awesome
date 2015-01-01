package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Client;
import models.Referral;
import models.UserModel;
import play.mvc.Controller;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import scala.util.parsing.json.JSONArray;
import scala.util.parsing.json.JSONArray$;
import scala.util.parsing.json.JSONObject$;
import views.html.*;

import java.util.Iterator;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.sort;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

/**
 * User: grant.mills
 * Date: 8/21/14
 * Time: 9:04 PM
 */
public class ReferralCtrl extends Controller {
    //Read Referral
	@BodyParser.Of(BodyParser.Json.class)
    public static Result getReferral(Long referralId) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");

        Referral currentReferral = Referral.getById(referralId);
        JsonNode referralJson = Json.toJson(currentReferral);

        result.put("data", referralJson);
        return ok(result);
    }

	//Create Referral
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addReferral() {
		Form<Referral> referralForm = Form.form(Referral.class);
		Referral referral = referralForm.bindFromRequest().get();

		referral.save();
		flash().put("success", "Your referral was created successfully");
		Logger.debug("Referral persisted with id: " + referral.id);
		response().setHeader(LOCATION, routes.ReferralCtrl.getReferral(referral.id).url());

		return status(201, "Created");
	}

	//Update Referral
	@BodyParser.Of(BodyParser.Json.class)
	public static Result editReferral() {
		Form<Referral> referralForm = Form.form(Referral.class);
		Referral referral = referralForm.bindFromRequest().get();

		referral.update();
		response().setHeader(LOCATION, routes.ReferralCtrl.getReferral(referral.id).url());

		return ok();
	}

	//Delete Referral
	@BodyParser.Of(BodyParser.Json.class)
	public static Result deleteReferral(Long refId) {
		Referral referral = Referral.getById(refId);

		if(null != referral) {
			//Status 200 - Resource succesfully deleted
			referral.delete();
			return ok();
		}else {
			return badRequest();
		}
	}

	//Aggregate Referrals by user Id and filter on freshness Bool
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getFreshReferrals(Long userId) {
		//Response Json object
		ObjectNode result = Json.newObject();

		//Get user from userId - USE HIS ASSIGNED REFERRALS
		UserModel currentUser = UserModel.getById(userId);

		//Filter Referrals to only the ones we care about - Fresh Ones
		List<Referral> freshReferrals = filter(having(on(Referral.class).fresh, equalTo(true)), currentUser.referrals);
		freshReferrals = sort(freshReferrals, on(Referral.class).nextStepDate);

		//Gather client data for each Referral
		JsonNode referralJson = gatherClientsForReferrals(freshReferrals);

		//Put data in the response object
		result.put("data", referralJson);
		return ok(result);
	}

	//Aggregate Referrals by their creator
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getReferralsByCreatorId(Long userId) {
		//Response Json object
		ObjectNode result = Json.newObject();

		//Get list of referrals created by User - NOT ASSIGNED TO USER
		List<Referral> referralsCreatedByUser = Referral.getByCreatorId(userId);

		//Gather client data for each Referral
		JsonNode referralJson = gatherClientsForReferrals(referralsCreatedByUser);

		//Put data in the response object
		result.put("data", referralJson);
		return ok(result);
	}

	//Gather client data for each Referral
	private static JsonNode gatherClientsForReferrals(List<Referral> referralList) {
		//Convert list to Json
		JsonNode referralJson = Json.toJson(referralList);

		//For each freshReferral retrieve client information and attach as "client" object in JSON
		for(Iterator<JsonNode> iter = referralJson.iterator(); iter.hasNext(); ) {
			//JsonNode is read only, ObjectNode is mutable
			ObjectNode ref = (ObjectNode) iter.next();

			//Lookup client
			Client clientModel = Client.getById(ref.get("clientId").longValue());

			//Create json node for client
			JsonNode client = Json.toJson(clientModel);

			ref.set("client", client);
		}

		return referralJson;
	}
}
