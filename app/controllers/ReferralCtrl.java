package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Referral;
import models.UserModel;
import play.mvc.Controller;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

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
	public static Result getFreshReferrals(Long userId) {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");

		//Get user from userId
		UserModel currentUser = UserModel.getById(userId);
		List<Referral> freshReferrals = filter(having(on(Referral.class).fresh, equalTo(true)), currentUser.referrals);
		freshReferrals = sort(freshReferrals, on(Referral.class).nextStepDate);

		JsonNode referralJson = Json.toJson(freshReferrals);

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

	@BodyParser.Of(BodyParser.Json.class)
	public static Result editReferral() {
		Form<Referral> referralForm = Form.form(Referral.class);
		Referral referral = referralForm.bindFromRequest().get();

		referral.update();
		response().setHeader(LOCATION, routes.ReferralCtrl.getReferral(referral.id).url());

		return ok();
	}
}
