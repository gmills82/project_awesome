import play.*;
import play.api.mvc.RequestHeader;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.SimpleResult;
import play.mvc.Http.*;
import play.libs.F.*;
import static play.mvc.Results.*;
import java.util.*;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        // Check if the database is empty
        if (UserModel.find.findRowCount() == 0) {
            Ebean.save((List) Yaml.load("initial-data.yml"));
        }
    }

	@Override
	public Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
		return Promise.<Result>pure(notFound(
			views.html.notFoundPage.render()
		));
	}

	@Override
	public Promise<Result> onError(Http.RequestHeader request, Throwable t) {
		return Promise.<Result>pure(ok(
			views.html.pageError.render()
		));
	}

	@Override
	public Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
		return Promise.<Result>pure(ok(
			views.html.pageError.render()
		));
	}
}