package controllers;

import models.*;
import models.Action;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

import java.util.List;

/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:22 AM
 */
public class UserCtrl extends Controller {

    //TODO: Convert to JSON routes
//    public static Result enterUser() {
//        Form<User> UserForm = Form.form(User.class);
//        List<User> UserList = User.getAll();
//
//        return ok(user.render(UserForm, UserList));
//    }
//
//    public static Result addUser() {
//        Form<User> UserForm = Form.form(User.class);
//        User User = UserForm.bindFromRequest().get();
//        User.save();
//
//        return redirect(routes.UserCtrl.enterUser());
//    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getActions(Long userId, String category) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");

        User currentUser = User.getById(userId);
        List<Action> actionList = Action.actionsByUserRole(currentUser.roleType.getPermissionLevel());
        if(null != actionList) {
            actionList = Action.filterByCategory(actionList, category);
        }
        //Convert actionList to an action json array
        JsonNode actions = Json.toJson(actionList);
        result.put("data", actions);

        return ok(result);
    }
}
