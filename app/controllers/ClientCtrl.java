package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.*;

import play.cache.Cache;
import play.data.validation.ValidationError;
import play.data.Form;


import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.*;
import scala.concurrent.stm.ccstm.Stats;
import utils.StatTotals;
import views.html.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:09 AM
 */
public class ClientCtrl extends Controller {
	public static final String CREATED = "Created";
	public static final String GOALS = "goals";
	public static final String YYYY_MM_DD = "yyyy-mm-dd";
	public static final String BIRTH_DATE_PRETTY = "birthDatePretty";
	public static final String STATUS = "status";
	public static final String OK = "OK";
	public static final String DATA = "data";
	/* JSON CRUD methods for Clients */

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getClientJSON(Long clientId) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");

        Client currentClient = Client.getById(clientId);
		ObjectNode clientJson = (ObjectNode) Json.toJson(currentClient);
		String[] goalsArray = currentClient.goalsString.split(" ");

		ObjectNode goalNode = Json.newObject();
		for(String goal : goalsArray) {
			if(goal != "false") {
				goalNode.put(goal, new String("true"));
			}
		}
		clientJson.put("goals", goalNode);

        result.put("data", clientJson);
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getClientHistoryRecords(Long clientId) {
        List<HistoryRecord> recordList = gatherClientHistory(clientId);
        HistoryRecords records = new HistoryRecords();
        records.setRecords(recordList);

        ObjectNode result = Json.newObject();
        result.put("data", Json.toJson(records));
        return ok(result);
    }

    @BodyParser.Of(play.mvc.BodyParser.Json.class)
     public static Result addClientJSON() throws ParseException {
        Form<Client> clientForm = Form.form(Client.class);
        Client client = clientForm.bindFromRequest().get();

        // Safeguard. Look up the last inserted client and check to see if the names match. If they do, there's a good
        // chance that the data was duplicated, so bail out of the request.
        // TODO - Better duplicate detection (Add timestamps and if multiple with same name within certain period of time)
		Client latestInsert = Client.getLastInsertedClient();
        if (latestInsert != null && latestInsert.name != null) {
            if (client.name != null && latestInsert.name.equalsIgnoreCase(client.name)) {
                return status(CONFLICT, "Duplicate client");
            }
        }

        JsonNode data = request().body().asJson();

        //Convert string date to java date
        if(null != data.findPath(BIRTH_DATE_PRETTY).textValue()) {
            client.birthDate = new SimpleDateFormat(YYYY_MM_DD).parse(data.findPath(BIRTH_DATE_PRETTY).textValue()).getTime();
        }
		if(null != data.findValue(GOALS)) {
			JsonNode goalsNode = data.findValue(GOALS);
			Iterator<Map.Entry<String, JsonNode>> iterator = goalsNode.fields();
			String tmpStr = "";
			while(iterator.hasNext()) {
				tmpStr += iterator.next().getKey() + " ";
			}
			client.goalsString = tmpStr;
		}

        client.save();
        response().setHeader(LOCATION, routes.ClientCtrl.getClientJSON(client.id).url());

        return status(201, CREATED);
    }

    @BodyParser.Of(play.mvc.BodyParser.Json.class)
    public static Result editClientJSON() {
        Form<Client> clientForm = Form.form(Client.class).bindFromRequest();

		JsonNode data = request().body().asJson();

        Client client = clientForm.get();

		JsonNode goalsNode = data.findPath(GOALS);

		if(!goalsNode.isMissingNode()) {
			String tmpStr = client.goalsString;

			//If goals object equals !false add it, if false find it in goal string and remove it
			Iterator<String> iter = goalsNode.fieldNames();
			//Iterate over fields of goals
			while(iter.hasNext()) {
				String key = iter.next();
				if(!key.equals(" ") && !goalsNode.findPath(key).isMissingNode()) {
					if(goalsNode.findPath(key).getNodeType().equals(JsonNodeType.BOOLEAN)) {
						tmpStr = tmpStr.replaceAll(key, "");
					}else {
						tmpStr += key + " ";
					}
				}
			}

			client.goalsString = tmpStr;
		}

        client.update();

        response().setHeader(LOCATION, routes.ClientCtrl.getClientJSON(client.id).url());

        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAssetTypes() {
        ObjectNode result = Json.newObject();
        result.put(STATUS, OK);

        List<FinancialAsset.AssetType> assetTypes = FinancialAsset.getAllAssetTypes();
        JsonNode types = Json.toJson(assetTypes);

        result.put(DATA, types);

        return ok(result);
    }

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getDebtTypes() {
		ObjectNode result = Json.newObject();
		result.put(STATUS, OK);

		List<Debt.DebtType> debtTypes = Debt.getAllDebtTypes();
		JsonNode types = Json.toJson(debtTypes);

		result.put(DATA, types);

		return ok(result);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result query(String queryString) {
		//Response object
		ObjectNode result = Json.newObject();
		Set<Client> clientList = new HashSet<Client>();
		Integer MINUTES_TO_CACHE = 1;

		//Check cache for request
		//If in cache attach that to results
		if(null != Cache.get(queryString)) {
			clientList = (Set<Client>) Cache.get(queryString);
		}else {
			//Else get requested data
			clientList = Client.query(queryString);

			//Add to cache
			Cache.set(queryString, clientList, 60 * MINUTES_TO_CACHE);
		}

		//Add to result
		result.set(DATA, Json.toJson(clientList));

		return ok(result);
	}

	public static List<HistoryRecord> gatherClientHistory(Long id) {
		List<HistoryRecord> historyModels = new ArrayList<HistoryRecord>();
		//Client history currently only includes Referrals
		historyModels.addAll(Referral.getByClientId(id));

		Collections.sort(historyModels);

		for(HistoryRecord record : historyModels) {

			//TODO: This was an if leftover from when records could be profiles
			if(record instanceof Referral) {
				Referral rRecord = (Referral) record;
				rRecord.setLink(routes.Application.editReferral(rRecord.id).url());
			}
		}

		return historyModels;
	}

	public static StatTotals sumRefferalStats(List<HistoryRecord> records) {
		Integer insuranceSum = 0;
		Integer ipsSum = 0;
		Integer pcSum = 0;

		for(HistoryRecord record: records) {
			if(record instanceof Referral) {
				Referral rRecord = ((Referral) record);
				insuranceSum += rRecord.tInsurance;
				ipsSum += rRecord.tIps;
				pcSum += rRecord.tPc;
			}
		}
		StatTotals stats = new StatTotals(insuranceSum, ipsSum, pcSum);
		return stats;
	}
}
