package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import java.util.*;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render());
    }

    public static Result profile() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
            return ok(profile.render(currentUser));
        }
        return redirect(routes.Application.login());
    }

	public static Result profileReview(Long profileId) {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			Profile reviewedProfile = Profile.getById(profileId);
			UserModel agent = UserModel.getById(reviewedProfile.agentId);
			Logger.debug("Agent number " + agent.id + " retrieved from profile");
			Client client = Client.getById(reviewedProfile.clientId);
			return ok(profileReview.render(agent, reviewedProfile, client));
		}
		return redirect(routes.Application.login());
	}

    public static Result producerScript() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
            List<Script> scriptList = Script.getAll();
            return ok(producerScript.render(currentUser, scriptList));
        }
        return redirect(routes.Application.login());
    }

    public static Result referral() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
            List<UserModel> faUsers = UserModel.getByPermissionLevel(UserModel.Role.FA);
            return ok(referral.render(currentUser, faUsers));
        }
        return redirect(routes.Application.login());
    }

    public static Result home() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
			//Collect a set of fresh referrals and send to homePage
			List<Referral> freshReferrals = filter(having(on(Referral.class).fresh, equalTo(true)), currentUser.referrals);
            return ok(homePage.render(currentUser, freshReferrals));
        }
        return redirect(routes.Application.login());
    }

    public static Result login() {
        Form<Login> loginForm = Form.form(Login.class);
        return ok(login.render(loginForm));
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
			Logger.debug("login has issues");
            return badRequest(login.render(loginForm));
        }else {
            session().clear();
            session("userName", loginForm.get().userName);
            return redirect(routes.Application.home());
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Application.login()
        );
    }

    public static Result signup(Integer roleType) {
        Form<UserModel> signupForm = Form.form(UserModel.class);
        return ok(signup.render(signupForm, roleType));
    }

    public static Result addSignup() {
        Form<UserModel> signupForm = Form.form(UserModel.class);
        UserModel newUser = signupForm.bindFromRequest().get();

        //Get request params
        Map<String, String[]> requestMap = request().body().asFormUrlEncoded();
        String[] confirmPassword = requestMap.get("confirmPassword");
        String[] userName = requestMap.get("userName");

        //Email already taken
        if(UserModel.isUserNameTaken(userName[0])) {
            signupForm.reject("That user name is already taken. Please try again.");
        }

        //Passwords don't match
        if(!(newUser.password.equals(confirmPassword[0]))){
            signupForm.reject("Your passwords do not match. Please retype them.");
        }else {
            //Salt password
            newUser.password = UserModel.saltPassword(newUser.password);
        }

		//Set Roletype
		Integer originalRoleType = Integer.parseInt(requestMap.get("roleTypeNum")[0]);
		Logger.debug(originalRoleType.getClass().toString() + originalRoleType);
		UserModel.setRoleType(newUser, originalRoleType);

		//Set parent team member to the User currently signing them up
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			newUser.parent_team_member = currentUser;
		}else {
			signupForm.reject("Error associating new team member with currently logged in team member.");
		}

        //Check form for errors
        if(signupForm.hasErrors()) {
            return badRequest(signup.render(signupForm, originalRoleType));
        }else {
            flash("success", "Please login using your new credentials.");
        }

        //Save user
        newUser.save();

        return redirect(routes.Application.login());
    }

    public static class Login {
        public String userName;
        public String password;

        //Called when we bind a Login from a request
        public String validate() {
			Logger.debug("userName in validate is: " + userName);
            String errors = UserModel.authenticate(userName, password);
            if(null == errors) {
                return null;
            }
            return errors;
        }
    }

    public static UserModel getCurrentUser() {
        String userName = session().get("userName");
        UserModel currentUser = null;
        if(null != userName) {
            currentUser = UserModel.getByEmail(userName);
        }
        return currentUser;
    }

}
