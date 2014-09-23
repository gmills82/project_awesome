package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Ref;
import java.util.Date;
import java.util.List;

/**
 * User: grant.mills
 * Date: 8/20/14
 * Time: 1:53 PM
 */
@Entity
public class Referral extends Model {
    @Id
    public long id;

    public long creatorId;
    public long agentId;
    public long clientId;

    public Long dateCreated = System.currentTimeMillis();
    public Date nextStepDate;

    public String reasonForReferral;
    public String creatorNotes;
    public Boolean wasProductive;

    public static Finder<Long, Referral> finder = new Finder(Long.class, Referral.class);
    public static Referral getById(Long id) {
        return finder.byId(id);
    }
    public static List<Referral> getAll() {
        return finder.all();
    }
    public static List<Referral> getByAgentId(Long id) {
        return finder.where().eq("agentId", id).findList();
    }
    public static List<Referral> getByCreatorId(Long id) {
        return finder.where().eq("creatorId", id).findList();
    }
}
