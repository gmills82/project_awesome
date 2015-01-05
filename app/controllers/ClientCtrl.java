package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.*;

import play.data.validation.ValidationError;
import play.data.Form;


import play.libs.Json;
import play.mvc.*;
import views.html.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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
			goalNode.put(goal, new String("true"));
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
        Logger.debug("Client persisted: " + client.name);
        response().setHeader(LOCATION, routes.ClientCtrl.getClientJSON(client.id).url());

        return status(201, "Created");
    }

    @BodyParser.Of(play.mvc.BodyParser.Json.class)
    public static Result editClientJSON() {
        Form<Client> clientForm = Form.form(Client.class).bindFromRequest();

        //There is no error on the form so it is now safe to get the Client
        Client client = clientForm.get();
		ObjectNode clientJson = (ObjectNode) Json.toJson(client);
		String[] goalsArray = client.goalsString.split(" ");

		ObjectNode goalNode = Json.newObject();
		for(String goal : goalsArray) {
			goalNode.put(goal, new String("true"));
		}
		clientJson.put("goals", goalNode);

        client.update();

        Logger.debug("Client updated: " + client.name);
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

//    /* Asset methods */
//
//    public static Result enterAsset(Long clientId) {
//        Client client = Client.getById(clientId);
//        if(null == client) {
//            return status(404);
//        }
//        Form<FinancialAsset> assetForm = Form.form(FinancialAsset.class);
//        List<FinancialAsset> assetsList = FinancialAsset.allForClient(client);
//        return ok(assets.render(assetsList, assetForm, client));
//    }
//
//    public static Result addAsset() {
//        Form<FinancialAsset> assetForm = Form.form(FinancialAsset.class);
//        FinancialAsset asset = assetForm.bindFromRequest().get();
//        asset.setRealAssetType(asset.assetTypeString);
//        asset.save();
//        return redirect(routes.ClientCtrl.enterAsset(asset.clientId));
//    }
//
//    /* Debt methods */
//
//    public static Result enterDebt(long clientId) {
//        Client client = Client.getById(clientId);
//        if(null == client) {
//            return status(400);
//        }
//        Form<Debt> debtForm = Form.form(Debt.class);
//        List<Debt> debtList = Debt.allForClient(client);
//        return ok(debts.render(debtList, debtForm, client));
//    }
//
//    public static Result addDebt() {
//        Form<Debt> debtForm = Form.form(Debt.class);
//        Debt debt = debtForm.bindFromRequest().get();
//        debt.setRealDebtType(debt.debtTypeString);
//        debt.save();
//        return redirect(routes.ClientCtrl.enterDebt(debt.clientId));
//    }
}
