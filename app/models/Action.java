package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.bean.EntityBean;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * User: grant.mills
 * Date: 8/7/14
 * Time: 12:11 PM
 */
@Entity
public class Action extends Model {
    @Id
    public long id;

    public String actionName;
    public String actionURL;
    public Integer requiredPermissionLevel;
    public String imageName;
    public String shortDescription;
    public String category;

    public static Finder<Integer, Action> find = new Finder(Integer.class, Action.class);

    public static List<Action> actionsByUserRole(Integer permissionLevel) {
        List<Action> allActions = find.all();
        Logger.debug("Total available actions: " + allActions.size());
        for(Action action : allActions) {
            Logger.debug("Action name: " + action.actionName);
        }
        Logger.debug("User's permission level was: " + permissionLevel);

        //Filter matches base level and all higher permission levels - 0 = root
        List<Action> filteredActions = Ebean.filter(Action.class).le("requiredPermissionLevel", permissionLevel).filter(allActions);
        Logger.debug("Filtered available actions: " + filteredActions.size());
        return filteredActions;
    }

    public static List<Action> filterByCategory(List<Action> actionList, String category) {
        return Ebean.filter(Action.class).eq("category", category).filter(actionList);
    }

    public static List<Action> allActions() {
        return find.all();
    }
}
