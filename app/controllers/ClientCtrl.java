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

    @BodyParser.Of(play.mvc.BodyParser.Json.class)
     public static Result addClientJSON() throws ParseException {
        Form<Client> clientForm = Form.form(Client.class);
        Client client = clientForm.bindFromRequest().get();

        JsonNode data = request().body().asJson();

        //Convert string date to java date
        if(null != data.findPath("birthDatePretty").textValue()) {
            client.birthDate = new SimpleDateFormat("yyyy-mm-dd").parse(data.findPath("birthDatePretty").textValue()).getTime();
        }
		if(null != data.findValue("goals")) {
			JsonNode goalsNode = data.findValue("goals");
			Iterator<Map.Entry<String, JsonNode>> iterator = goalsNode.fields();
			String tmpStr = "";
			while(iterator.hasNext()) {
				tmpStr += iterator.next().getKey() + " ";
			}
			client.goalsString = tmpStr;
		}

        client.save();
        response().setHeader(LOCATION, routes.ClientCtrl.getClientJSON(client.id).url());

        return status(201, "Created");
    }

    @BodyParser.Of(play.mvc.BodyParser.Json.class)
    public static Result editClientJSON() {
        Form<Client> clientForm = Form.form(Client.class).bindFromRequest();

		JsonNode data = request().body().asJson();

        Client client = clientForm.get();

		JsonNode goalsNode = data.findPath("goals");

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
        result.put("status", "OK");

        List<FinancialAsset.AssetType> assetTypes = FinancialAsset.getAllAssetTypes();
        JsonNode types = Json.toJson(assetTypes);

        result.put("data", types);

        return ok(result);
    }

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getDebtTypes() {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");

		List<Debt.DebtType> debtTypes = Debt.getAllDebtTypes();
		JsonNode types = Json.toJson(debtTypes);

		result.put("data", types);

		return ok(result);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result query(String queryString) {
		//Response object
		ObjectNode result = Json.newObject();
		Set<Client> clientList = new HashSet<Client>();
		Integer MINUTES_TO_CACHE = 10;

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
		result.set("data", Json.toJson(clientList));

		return ok(result);
	}

	public static List<HistoryRecord> gatherClientHistory(Long id) {
		List<HistoryRecord> historyModels = new ArrayList<HistoryRecord>();
		historyModels.addAll(Referral.getByClientId(id));
		historyModels.addAll(Profile.getByClientId(id));

		Collections.sort(historyModels);

		for(HistoryRecord record: historyModels) {
			if(record instanceof Referral) {
				Referral rRecord = (Referral) record;
				rRecord.setLink(routes.Application.editReferral(rRecord.id).url());
			}else if(record instanceof Profile) {
				Profile pRecord = (Profile) record;
				pRecord.setLink(routes.Application.profileReview(pRecord.id).url());
			}
		}

		return historyModels;
	}
}
