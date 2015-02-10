import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import play.*;
import play.api.mvc.Handler;
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

		//Set filter for password
		//Create filterProvider which contains a password filter
		FilterProvider filters = new SimpleFilterProvider().addFilter("password", SimpleBeanPropertyFilter.serializeAllExcept("password"));
		//Pass filterProvider to object mapper
		ObjectMapper mapper = new ObjectMapper();
		mapper.setFilters(filters);
		//Tell play's helper class to user our object mapper not theirs
		Json.setObjectMapper(mapper);
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

	@Override
	public Handler onRouteRequest(Http.RequestHeader request) {
		String[] x = request.headers().get("X-Forwarded-Proto");
		if (Play.isProd() && (x == null || x.length == 0 || x[0] == null || !x[0].contains("https")))
			return controllers.Default.redirect("https://" + request.host() + request.uri());
		return super.onRouteRequest(request);
	}
}