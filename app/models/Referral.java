package models;

import com.avaje.ebean.OrderBy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: grant.mills
 * Date: 8/20/14
 * Time: 1:53 PM
 */
@Entity
public class Referral extends HistoryRecord {
    @Id
    public long id;

    public long creatorId;
	public long clientId;
	public String clientName;
	public String refType;
	public String status = "OPEN";
	public Integer tInsurance = 0;
	public Integer tPc = 0;
	public Integer tIps = 0;

    public long user_id;

    public Long dateCreated = System.currentTimeMillis();
	public String nextStepDate;
	public String lastEditedDate;

    public String reasonForReferral;
    public String refNotes;
    public Boolean wasProductive = false;


    public static Finder<Long, Referral> finder = new Finder(Long.class, Referral.class);
    public static Referral getById(Long id) {
        return finder.byId(id);
    }
    public static List<Referral> getAll() {
        return finder.all();
    }
    public static List<Referral> getByCreatorId(Long id) {
        return finder.where().eq("creatorId", id).findList();
    }
	public static List<Referral> getByAssignedIdInRange(Long assigneeId, Long range) {
		return finder.where().eq("user_id", assigneeId).ge("dateCreated", range).findList();
	}
	public static List<Referral> getByUserId(Long assignedUserId) {
		return finder.where().eq("user_id", assignedUserId).findList();
	}
	public static List<Referral> getByClientId(Long id) {
		return finder.where().eq("clientId", id).findList();
	}

	public String getLastEditedDate() {
		return lastEditedDate;
	}

	public void setLastEditedDate(String lastEditedDate) throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
		Date d = f.parse(lastEditedDate);
		long milliseconds = d.getTime();

		super.setDateOfLastInteraction(milliseconds);
		this.lastEditedDate = lastEditedDate;
	}
}
