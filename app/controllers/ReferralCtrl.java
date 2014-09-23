package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Referral;
import play.mvc.Controller;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

/**
 * User: grant.mills
 * Date: 8/21/14
 * Time: 9:04 PM
 */
public class ReferralCtrl extends Controller {
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getReferral(Long referralId) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");

        Referral currentReferral = Referral.getById(referralId);
        JsonNode referralJson = Json.toJson(currentReferral);

        result.put("data", referralJson);
        return ok(result);
    }

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
}
