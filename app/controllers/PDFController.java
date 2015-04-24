package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * User: grant.mills
 * Date: 4/24/15
 * Time: 7:21 PM
 */
public class PDFController extends Controller {
	public static Result newAccount(Long clientId) {

		return redirect(Application.login());
	}
}