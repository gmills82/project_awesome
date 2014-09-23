package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * User: grant.mills
 * Date: 8/20/14
 * Time: 2:22 PM
 */
@Entity
public class Script extends Model {
    @Id
    public long id;
    public String type;
    public String heading;
    public String body;

    public static Finder<Long, Script> finder = new Finder(Long.class, Script.class);

    public static List<Script> getAll() {
        return finder.all();
    }

    public static List<Script> getByType(String type) {
        return finder.where().eq("type", type).findList();
    }
}
