package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import models.Client;
import models.Referral;
import models.UserModel;
import play.Logger;
import play.mvc.Controller;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import utils.DateUtilities;

import java.text.SimpleDateFormat;
import java.util.*;

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
		setWasProductive(referral);

		referral.save();
		flash().put("success", "Your referral was created successfully");
		response().setHeader(LOCATION, routes.ReferralCtrl.getReferral(referral.id).url());

		return status(201, "Created");
	}

	//Update Referral
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateReferral() {
		Form<Referral> referralForm = Form.form(Referral.class);
		Referral referral = referralForm.bindFromRequest().get();
		setWasProductive(referral);

		JsonNode data = request().body().asJson();
		JsonNode refStatusNode = data.findValue("status");
		referral.status = refStatusNode.get("status").textValue();

		//Map agentId to user_id
		if(null != data.findValue("agentId")){
			Long agent = Long.parseLong(data.findValue("agentId").textValue());
			referral.user_id = agent;
		}

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

		response().setHeader("Cache-Control", "max-age=180");
		String previousEtag = null;
		if(null != request().getHeader("If-None-Match")) {
			previousEtag = request().getHeader("If-None-Match");
		}

		//Get user from userId - USE HIS ASSIGNED REFERRALS
		UserModel currentUser = UserModel.getById(userId);

		//Today's date
		SimpleDateFormat sdf = DateUtilities.getDateFormat();
		Calendar cal = new GregorianCalendar();
		String startDate = sdf.format(cal.getTime());
		List<Referral> currentUsersRefs = Referral.getByUserIdNotInFuture(currentUser.id, startDate);

		//Filter Referrals to only the ones we care about - Status = "OPEN"
		List<Referral> freshReferrals = filter(having(on(Referral.class).status, equalTo("OPEN")), currentUsersRefs);

		//TODO: More caching cleanup needed
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).nextStepDate);

			//Gather client data for each Referral
			JsonNode referralJson = gatherClientsForReferrals(freshReferrals);
			for (JsonNode referral : referralJson) {
				ObjectNode refObj = (ObjectNode) referral;
				UserModel creator = UserModel.getById(referral.findPath("creatorId").asLong());
				refObj.set("creatorName", Json.toJson(creator.firstName + " " + creator.lastName));
			}

			//Put data in the response object
			result.put("data", referralJson);
			response().setHeader("ETag", etag);
			return ok(result);
		}
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getUpcomingReferrals(long userId) {
		//Response Json object
		ObjectNode result = Json.newObject();

		response().setHeader("Cache-Control", "max-age=180");
		String previousEtag = null;
		if(null != request().getHeader("If-None-Match")) {
			previousEtag = request().getHeader("If-None-Match");
		}

		//Get user from userId - USE HIS ASSIGNED REFERRALS
		UserModel currentUser = UserModel.getById(userId);

		//Create date string 7 days in future
		SimpleDateFormat sdf = DateUtilities.getDateFormat();
		Calendar cal = new GregorianCalendar();
		String startDate = sdf.format(cal.getTime());
		cal.add(Calendar.DATE, 7);
		String endDate = sdf.format(cal.getTime());
		List<Referral> currentUsersRefs = Referral.getReferralsByIdInRange(currentUser.id, startDate, endDate);

		//Filter Referrals to only the ones we care about - Status = "OPEN"
		List<Referral> freshReferrals = filter(having(on(Referral.class).status, equalTo("OPEN")), currentUsersRefs);

		//Etag caching
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).nextStepDate);

			//Gather client data for each Referral
			JsonNode referralJson = gatherClientsForReferrals(freshReferrals);
			for (JsonNode referral : referralJson) {
				ObjectNode refObj = (ObjectNode) referral;
				UserModel creator = UserModel.getById(referral.findPath("creatorId").asLong());
				refObj.set("creatorName", Json.toJson(creator.firstName + " " + creator.lastName));
			}

			//Put data in the response object
			result.put("data", referralJson);
			response().setHeader("ETag", etag);
			return ok(result);
		}
	}


	@BodyParser.Of(BodyParser.Json.class)
	public static Result getUpcomingAppts(long userId) {
		//Response Json object
		ObjectNode result = Json.newObject();

		response().setHeader("Cache-Control", "max-age=180");
		String previousEtag = null;
		if(null != request().getHeader("If-None-Match")) {
			previousEtag = request().getHeader("If-None-Match");
		}

		//Get user from userId - USE HIS ASSIGNED REFERRALS
		UserModel currentUser = UserModel.getById(userId);

		//Create date string 7 days in future
		SimpleDateFormat sdf = DateUtilities.getDateFormat();
		Calendar cal = new GregorianCalendar();
		String startDate = sdf.format(cal.getTime());
		cal.add(Calendar.DATE, 7);
		String endDate = sdf.format(cal.getTime());
		List<Referral> currentUsersRefs = Referral.getApptsByIdInRange(currentUser.id, startDate, endDate);

		//Filter Referrals to only the ones we care about - Status = "OPEN"
		List<Referral> freshReferrals = filter(having(on(Referral.class).status, equalTo("OPEN")), currentUsersRefs);

		//Etag caching
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).nextStepDate);

			//Gather client data for each Referral
			JsonNode referralJson = gatherClientsForReferrals(freshReferrals);
			for (JsonNode referral : referralJson) {
				ObjectNode refObj = (ObjectNode) referral;
				UserModel creator = UserModel.getById(referral.findPath("creatorId").asLong());
				refObj.set("creatorName", Json.toJson(creator.firstName + " " + creator.lastName));
			}

			//Put data in the response object
			result.put("data", referralJson);
			response().setHeader("ETag", etag);
			return ok(result);
		}
	}

	//Aggregate Referrals by their creator
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getReferralsByCreatorId(Long userId) {
		//Response Json object
		ObjectNode result = Json.newObject();

		//Get list of referrals created by User - NOT THOSE ASSIGNED TO USER
		List<Referral> referralsCreatedByUser = Referral.getRecentByCreatorId(userId);

		//Gather client data for each Referral
		JsonNode referralJson = gatherClientsForReferrals(referralsCreatedByUser);

		//Put data in the response object
		result.put("data", referralJson);

		return ok(result);
	}

	//Aggregate Referrals of all team members based on parent team member
	@BodyParser.Of(BodyParser.Json.class)
	public static Result getTeamReferralsByParentId(Long userId) {
		//Response object
		ObjectNode result = Json.newObject();

		UserModel parent = UserModel.getById(userId);
		//Gather child team memebers
		Set<UserModel> teamMembers = gatherChildTeamMembers(parent);

		//Add referrals created by each team member
		Set<Referral> allReferrals = gatherAllReferralsForTeam(teamMembers);

		JsonNode allReferralsNode = gatherClientsForReferrals(allReferrals);

		//Match UserModel of the creator of the referral to the referral
		for(int x = 0; x < allReferralsNode.size(); x++) {
			//For each referral, iterate through teamMembers and attach them to referral
			Iterator<UserModel> iter = teamMembers.iterator();
			while(iter.hasNext()) {
				UserModel tmp = iter.next();
				if(allReferralsNode.hasNonNull(x) && allReferralsNode.get(x).hasNonNull("creatorId")) {
					if(allReferralsNode.get(x).get("creatorId").longValue() == tmp.id) {
						//Convert to Json
						JsonNode tmpUserNode = Json.toJson(tmp);
						ObjectNode tmpRefNode = (ObjectNode) allReferralsNode.get(x);
						tmpRefNode.put("creator", tmpUserNode);
					}
				}
			}
		}

		//Append to response
		result.put("data", allReferralsNode);

		return ok(result);
	}

	private static Set<UserModel> gatherChildTeamMembers(UserModel parent) {
		//Unique team members collection
		Set<UserModel> team = UserModel.getChildUserModelsByParentAllLevels(parent);
		team.add(parent);
		return team;
	}

	private static Set<Referral> gatherAllReferralsForTeam(Set<UserModel> teamMembers) {
		//Set to return
		Set<Referral> referrals = new HashSet<Referral>();

		//Iterate through teamMembers
		Iterator<UserModel> uIter = teamMembers.iterator();
		while(uIter.hasNext()) {
			//Current team member
			UserModel currentTeamMember = uIter.next();

			//Add list of referrals they created to all referrals
			List<Referral> createdReferrals = Referral.getByCreatorId(currentTeamMember.id);
			referrals.addAll(createdReferrals);
		}


		//HashSet refuses dups
		return referrals;
	}

	//Gather client data for each Referral
	private static JsonNode gatherClientsForReferrals(Collection<Referral> referralList) {
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

	private static void setWasProductive(Referral referral) {
		if(referral.tInsurance > 0) {
			referral.wasProductive = true;
		}
		if(referral.tIps > 0) {
			referral.wasProductive = true;
		}
		if(referral.tPc > 0) {
			referral.wasProductive = true;
		}
	}

	/**
	 * Update Open Declined Referrals to Closed. Used nightly
	 */
	public static void cleanOpenDeclinedReferrals() {
		List<Referral> decRefs = Referral.getOpenDeclinedReferrals();
		for(Referral ref: decRefs) {
			ref.status = "CLOSED";
			ref.save();
		}
	}
}
