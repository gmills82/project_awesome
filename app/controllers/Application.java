package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.cache.Cached;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import org.mindrot.jbcrypt.BCrypt;

import utils.MailUtilities;
import utils.StatTotals;
import views.html.*;

import java.util.*;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render());
    }

    public static Result producerScript() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
            return ok(producerScript.render(currentUser));
        }
        return redirect(routes.Application.login());
    }

    public static Result referral() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {
			List<UserModel> team = gatherAssignableTeamMembers(currentUser);
            return ok(referral.render(currentUser, team));
        }
        return redirect(routes.Application.login());
    }

	public static Result editReferral(Long refId) {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			List<UserModel> team = gatherAssignableTeamMembers(currentUser);
			return ok(editReferral.render(currentUser, team));
		}
		return redirect(routes.Application.login());
	}

    public static Result home() {
        UserModel currentUser = getCurrentUser();
        if(null != currentUser) {

            return ok(homePage.render(currentUser));
        }
        return redirect(routes.Application.login());
    }

	@Cached(key= "loginPage", duration = 60 * 60)
    public static Result login() {
        Form<Login> loginForm = Form.form(Login.class);
        return ok(login.render(loginForm));
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
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
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {

			Form<UserModel> signupForm = Form.form(UserModel.class);
			if (roleType < 0 || roleType > 2) {
				return badRequest(pageError.render());
			} else {
				//If currentUser is allowed to use signup action
				if(currentUser.roleType.getPermissionLevel() < roleType) {
					return ok(signup.render(currentUser, signupForm, roleType));
				}else {
					return badRequest(pageError.render());
				}
			}

		}return redirect(routes.Application.login());
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
            return badRequest(signup.render(currentUser, signupForm, originalRoleType));
        }else {
            flash("success", "Please login using your new credentials.");
        }

        //Save user
        newUser.save();


        return redirect(routes.Application.home());
    }

	public static Result teamReferrals() {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {

			return ok(teamReferrals.render(currentUser));
		}
		return redirect(routes.Application.login());
	}

	public static Result clients() {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			return ok(clients.render(currentUser));
		}
		return redirect(routes.Application.login());
	}


	public static Result clientHistory(Long clientId) {
		Client client = Client.getById(clientId);
		List<HistoryRecord> clientHistoryRecords = ClientCtrl.gatherClientHistory(clientId);
		StatTotals clientSums = ClientCtrl.sumRefferalStats(clientHistoryRecords);

		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			return ok(clientHistory.render(currentUser, client, clientHistoryRecords, clientSums));
		}
		return redirect(routes.Application.login());
	}

	public static Result pay() {
		return ok(pay.render());
	}

    /**
     * Dummy function that shows how to use the email utilities
     * @return
     */
	public static Result email() {
		MailUtilities mail = new MailUtilities();
		mail.sendEmail("admin@efsmanager.com", "gmills82@gmail.com", "Test from javax", "hello email world");
		return ok();
	}

	public static Result passwordChange() {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			return ok(passwordChange.render(currentUser));
		}
		return redirect(routes.Application.login());
	}

	public static Result passwordChangeSubmit() {
		UserModel currentUser = getCurrentUser();
		if(null != currentUser) {
			DynamicForm dynamicForm = Form.form().bindFromRequest();
			if(BCrypt.checkpw(dynamicForm.get("oldPassword"), currentUser.password)) {
				//if oldPassword matches - change the password
				String saltedPass = UserModel.saltPassword(dynamicForm.get("password"));
				currentUser.password = saltedPass;
				currentUser.update();
				//Flash success message and return to dashboard
				flash("success", "You password has been successfully changed.");
				return redirect(routes.Application.home());
			}else {
				//else - return to old page with a flash error message
				flash("error", "The password you entered was incorrect");
				return ok(passwordChange.render(currentUser));
			}
		}
		return redirect(routes.Application.login());
	}


	public static class Login {
        public String userName;
        public String password;

        //Called when we bind a Login from a request
        public String validate() {
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

	private static List<UserModel> gatherAssignableTeamMembers(UserModel currentUser) {
		//List of agents should contain parent team members (Agents) and their parent team members (FA)
		List<UserModel> assignableTeamMembers = new ArrayList<UserModel>();
		//If this gets more complex make it recursive
		if(null != currentUser.parent_team_member) {
			assignableTeamMembers.add(currentUser.parent_team_member);
			if(null != assignableTeamMembers.get(0).parent_team_member) {
				assignableTeamMembers.add(assignableTeamMembers.get(0).parent_team_member);
			}
		}

		//If currentUser is FA of Agent assignable to self
		if(currentUser.roleType.getPermissionLevel() <= 1) {
			assignableTeamMembers.add(currentUser);

			//Get all descendant users
			Set<UserModel> allAgents = UserModel.getChildUserModelsByParentAllLevels(currentUser);
			assignableTeamMembers.addAll(allAgents);
		}

		return assignableTeamMembers;
	}
}
