package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import models.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.mvc.Controller;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import utils.DateUtilities;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

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

        if (currentReferral == null) {
            return notFound();
        }

        // Look up any notes for this referral and add them to the return data
        currentReferral.setReferralNotes(ReferralNote.getByReferralId(referralId));
        if (currentReferral.getReferralNotes() != null) {
            for (ReferralNote note : currentReferral.getReferralNotes()) {
                if (note.getUserModelId() == null || note.getUserModel() != null) {
                    continue;
                }
                note.setUserModel(UserModel.getById(note.getUserModelId()));
            }
        }

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

        // If the referral has a note assigned, save it off and remove it from the referral so it doesn't get persisted
        // with the referral row.
        String referralNote = null;
        if (StringUtils.trimToNull(referral.getRefNotes()) != null) {
            referralNote = referral.getRefNotes();
            referral.setRefNotes(null);
        }

		referral.save();

        // After the referral has been saved, check to see if we have a note that was pulled. If so, save it off and
        // assign the referral ID to it.
        if (referralNote != null) {
            ReferralNote note = new ReferralNote();
            note.setNote(referralNote);
            note.setCreatedDate(new Date());
            note.setUserModelId(referral.getCreatorId());
            note.setReferralId(referral.id);
            note.save();
        }

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

        // EBeans treat null values as "unloaded" as a protection against unset fields in updates. In this case, we want
        // to watch for a null value, so we'll make an extra query if that's the case.
        if (referral.getApptKept() == null) {
            Referral.setApptKeptById(referral.id, referral.getApptKept());
        }

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
		List<Referral> freshReferrals = filter(having(on(Referral.class).getRecordStatus(), equalTo("OPEN")), currentUsersRefs);

		//TODO: More caching cleanup needed
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).getNextStepTimestamp());

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
		List<Referral> freshReferrals = filter(having(on(Referral.class).getRecordStatus(), equalTo("OPEN")), currentUsersRefs);

		//Etag caching
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).getNextStepTimestamp());

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
		List<Referral> freshReferrals = filter(having(on(Referral.class).getRecordStatus(), equalTo("OPEN")), currentUsersRefs);

		//Etag caching
		String etag = ((Integer) freshReferrals.hashCode()).toString();
		if(null != previousEtag && previousEtag.equals(etag)) {
			return status(304);

		}else {
			//Continue on since something has changed
			freshReferrals = sort(freshReferrals, on(Referral.class).getNextStepTimestamp());

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

        List<Referral> returnReferrals = new ArrayList<>(allReferrals);
        Integer totalReferrals = returnReferrals.size();

        // Get all the clients for each of the referrals and put them into a map to be looked up and assigned to the
        // appropriate referral.
        Map<Long, Client> clientMap = new HashMap<>();
        List<Client> clients = Client.getByIds(
                allReferrals.stream().map(Referral::getClientId).collect(Collectors.toList())
        );
        for (Client client : clients) {
            clientMap.put(client.getId(), client);
        }
        for (Referral referral : returnReferrals) {
            referral.setClient(clientMap.get(referral.getClientId()));
        }

        // Filtering by client name...
        String clientName = StringUtils.trimToNull(request().getQueryString("clientName"));
        if (clientName != null) {

            //  I <3 Java 8
            returnReferrals = returnReferrals
                    .stream()
                    .filter(p -> p.getClientName().startsWith(clientName))
                    .collect(Collectors.toList());
        }

        // Filtering by referral type...
        String referralType = StringUtils.trimToNull(request().getQueryString("refType"));
        if (referralType != null) {
            returnReferrals = returnReferrals
                    .stream()
                    .filter(p -> p.getRefType().equalsIgnoreCase(referralType))
                    .collect(Collectors.toList());
        }

        // Sorting...
        // FIXME There has to be a better way to do this. Maybe with reflection?
        String sort = StringUtils.trimToNull(request().getQueryString("sort"));
        if (sort != null) {
            Collections.sort(returnReferrals, (o1, o2) -> {

                // Sort ascending if the first character is a '+'
                Boolean asc = sort.substring(0, 1).equals("+");
                String sortValue = sort.substring(1);

                String property1 = null;
                String property2 = null;

                if (sortValue.equalsIgnoreCase("clientName")) {
                    property1 = o1.getClientName().toLowerCase();
                    property2 = o2.getClientName().toLowerCase();
                }
                else if (sortValue.equalsIgnoreCase("refType")) {
                    property1 = o1.getRefType().toLowerCase();
                    property2 = o2.getRefType().toLowerCase();
                }

                // String comparitors...
                if (StringUtils.trimToNull(property1) != null && StringUtils.trimToNull(property2) != null) {
                    if (asc) {
                        return property1.compareTo(property2);
                    }
                    return property2.compareTo(property1);
                }

                Long long1 = null;
                Long long2 = null;
                if (sortValue.equalsIgnoreCase("nextStepDate")) {
                    long1 = DateUtilities.normalizeDateString(o1.getNextStepDate()).getTime();
                    long2 = DateUtilities.normalizeDateString(o2.getNextStepDate()).getTime();
                }
                else if (sortValue.equalsIgnoreCase("client.phoneNumber")) {
                    if (o1.getClient() != null && StringUtils.trimToNull(o1.getClient().getPhoneNumber()) != null) {
                        long1 = Long.valueOf(o1.getClient().getPhoneNumber().replaceAll("\\D+", ""));
                    }
                    if (o2.getClient() != null && StringUtils.trimToNull(o2.getClient().getPhoneNumber()) != null) {
                        long2 = Long.valueOf(o2.getClient().getPhoneNumber().replaceAll("\\D+", ""));
                    }
                }
                else if (sortValue.equalsIgnoreCase("tInsurance")) {
                    long1 = Long.valueOf(o1.gettInsurance());
                    long2 = Long.valueOf(o2.gettInsurance());
                }
                else if (sortValue.equalsIgnoreCase("tPc")) {
                    long1 = Long.valueOf(o1.gettPc());
                    long2 = Long.valueOf(o2.gettPc());
                }
                else if (sortValue.equalsIgnoreCase("tIps")) {
                    long1 = Long.valueOf(o1.gettIps());
                    long2 = Long.valueOf(o2.gettIps());
                }
                else if (sortValue.equalsIgnoreCase("dateOfLastInteraction")) {
                    long1 = o1.getDateOfLastInteraction();
                    long2 = o2.getDateOfLastInteraction();
                }

                // Number comparitors
                if (long1 != null && long2 != null) {
                    if (asc) {
                        return long1.compareTo(long2);
                    }
                    return long2.compareTo(long1);
                }
                return 0;
            });
        }

        // Offset & limit
        String offset = StringUtils.trimToNull(request().getQueryString("offset"));
        String limit = StringUtils.trimToNull(request().getQueryString("limit"));
        if (offset == null) {
            offset = "0";
        }
        if (limit == null) {
            limit = "10";
        }

        returnReferrals = returnReferrals
                .stream()
                .skip(Long.valueOf(offset))
                .limit(Long.valueOf(limit))
                .collect(Collectors.toList());

        ReferralList referralList = new ReferralList();
        referralList.setReferrals(returnReferrals);
        referralList.setTotal(totalReferrals);

		//Append to response
		result.put("data", new ObjectMapper().convertValue(referralList, JsonNode.class));

		return ok(result);
	}

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addNoteToReferral() {

        Form<ReferralNote> referralNoteForm = Form.form(ReferralNote.class);
        ReferralNote referralNote = referralNoteForm.bindFromRequest().get();

        // Why won't this get set automatically?
        referralNote.setCreatedDate(new Date());
        referralNote.save();

        // Updated the latest interaction date for the referral
        Referral referral = Referral.getById(referralNote.getReferralId());
        if (referral != null) {

            // Why is this a string?
            referral.setLastEditedDate(String.valueOf(new Date().getTime()));
            referral.save();
        }

        // Get the last inserted note to return
        ObjectNode result = Json.newObject();
        ReferralNote addedNote = ReferralNote.getById(referralNote.getId());
        addedNote.setUserModel(UserModel.getById(addedNote.getUserModelId()));
        result.put("data", Json.toJson(addedNote));
        return status(201, result);
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
