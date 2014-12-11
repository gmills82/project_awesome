package controllers;

import models.*;
import models.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;

import java.util.List;

/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:22 AM
 */
public class UserCtrl extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getActions(Long userId, String category) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");

        UserModel currentUser = UserModel.getById(userId);
        List<Action> actionList = Action.actionsByUserRole(currentUser.roleType.getPermissionLevel());
        if(null != actionList) {
            actionList = Action.filterByCategory(actionList, category);
        }
        //Convert actionList to an action json array
        JsonNode actions = Json.toJson(actionList);
        result.put("data", actions);

        return ok(result);
    }

	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateUser() {
		Form<UserModel> userForm = Form.form(UserModel.class).bindFromRequest();

		//There is no error on the form so it is now safe to get the User
		UserModel user = userForm.get();
		user.update();

		Logger.debug("User updated: " + user.userName);
		response().setHeader(LOCATION, routes.UserCtrl.getUser(user.id).url());

		return ok();
	}
	

	@BodyParser.Of(BodyParser.Json.class)
	public static Result getUser(Long userId) {
		ObjectNode result = Json.newObject();
		result.put("status", "OK");

		UserModel currentUser = UserModel.getById(userId);
		if(null == currentUser) {
			return badRequest("User not found");
		}
		JsonNode user = Json.toJson(currentUser);
		result.put("data", user);
		
		return ok(result);
	}
}
