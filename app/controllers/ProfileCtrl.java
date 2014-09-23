package controllers;

import play.mvc.Controller;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

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
}
