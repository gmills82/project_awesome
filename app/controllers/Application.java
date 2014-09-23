package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import java.util.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render());
    }

    public static Result profile() {
        User currentUser = getCurrentUser();
        if(null != currentUser) {
            return ok(profile.render(currentUser));
        }
        return redirect(routes.Application.index());
    }

    public static Result producerScript() {
        User currentUser = getCurrentUser();
        if(null != currentUser) {
            List<Script> scriptList = Script.getAll();
            return ok(producerScript.render(currentUser, scriptList));
        }
        return redirect(routes.Application.index());
    }

    public static Result referral() {
        User currentUser = getCurrentUser();
        if(null != currentUser) {
            List<User> faUsers = User.getByPermissionLevel(User.Role.FA);
            return ok(referral.render(currentUser, faUsers));
        }
        return redirect(routes.Application.index());
    }

    public static Result home() {
        User currentUser = getCurrentUser();
        if(null != currentUser) {
            return ok(homePage.render(currentUser));
        }
        return redirect(routes.Application.index());
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
            session("email", loginForm.get().email);
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

    public static Result signup() {
        Form<User> signupForm = Form.form(User.class);
        return ok(signup.render(signupForm));
    }

    public static Result addSignup() {
        Form<User> signupForm = Form.form(User.class);
        User newUser = signupForm.bindFromRequest().get();

        //Get request params
        Map<String, String[]> requestMap = request().body().asFormUrlEncoded();
        String[] confirmPassword = requestMap.get("confirmPassword");
        String[] userName = requestMap.get("userName");

        //Email already taken
        if(User.isUserNameTaken(userName[0])) {
            signupForm.reject("That user name is already taken. Please try again.");
        }

        //Passwords don't match
        if(!(newUser.password.equals(confirmPassword[0]))){
            signupForm.reject("Your passwords do not match. Please retype them.");
        }else {
            //Salt password
            newUser.password = User.saltPassword(newUser.password);
        }

        //Set Roletype
        String[] roleTypeString = requestMap.get("roleTypeString");
        User.setRoleType(newUser, roleTypeString[0]);

        //Check form for errors
        if(signupForm.hasErrors()) {
            return badRequest(signup.render(signupForm));
        }else {
            flash("success", "Please login using your new credentials.");
        }

        //Save user
        newUser.save();

        return redirect(routes.Application.login());
    }

    public static class Login {
        public String email;
        public String password;

        //Called when we bind a Login from a request
        public String validate() {
            String errors = User.authenticate(email, password);
            if(null == errors) {
                return null;
            }
            return errors;
        }
    }

    public static User getCurrentUser() {
        String userName = session().get("email");
        User currentUser = null;
        if(null != userName) {
            currentUser = User.getByEmail(userName);
        }
        return currentUser;
    }

}
