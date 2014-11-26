package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	public long clientId;
	public String clientName;
	public String refType;

	@ManyToOne(optional = false)
	@JoinColumn(name="user", referencedColumnName = "id")
	@JsonBackReference
    public User user;

    public Long dateCreated = System.currentTimeMillis();
	public String nextStepDate;

    public String reasonForReferral;
    public String refNotes;
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
