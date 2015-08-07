package models;

import com.avaje.ebean.Expr;
import org.joda.time.DateTime;
import play.Logger;
import play.db.ebean.Model;
import utils.DateUtilities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: grant.mills
 * Date: 8/20/14
 * Time: 1:53 PM
 */
@Entity
public class Referral extends Model implements HistoryRecord {
	public static final int MILLISECONDS_IN_A_DAY = 86400000;

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

	@Transient
	public String link;

    public long user_id;

    public Long dateCreated = System.currentTimeMillis();

    /**
     @deprecated
     The nextStepDate yields un-normalized results. Use nextStepTimestamp instead and format in the client.
     */
    @Deprecated
	public String nextStepDate;

	private Date nextStepTimestamp;
	public String lastEditedDate;

    public String reasonForReferral;
    public String refNotes;
	public String advisorRecommendation;
    public Boolean wasProductive = false;


    public static Model.Finder<Long, Referral> finder = new Model.Finder(Long.class, Referral.class);
    public static Referral getById(Long id) {
        return finder.byId(id);
    }
    public static List<Referral> getAll() {
        return finder.all();
    }
    public static List<Referral> getRecentByCreatorId(Long id) {
		int MAX_RECENT_REFERRALS = 25;
        return finder.where().eq("creatorId", id).orderBy("date_created DESC").setMaxRows(MAX_RECENT_REFERRALS).findList();
    }
    public static List<Referral> getByCreatorId(Long id) {
		int MAX_RECENT_REFERRALS = 25;
        return finder.where().eq("creatorId", id).orderBy("id").setMaxRows(MAX_RECENT_REFERRALS).findList();
    }
	public static List<Referral> getByAssignedIdInRange(Long assigneeId, Long range) {
		return finder.where().eq("user_id", assigneeId).ge("dateCreated", range).findList();
	}

	/**
	 * Used to find referrals assigned to a user that have next step dates within a specified date range. Filters out Appts
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Referral> getReferralsByIdInRange(Long userId, String startDate, String endDate) {
		return finder.where().eq("user_id", userId).ne("ref_type", "Appt").ge("nextStepDate", startDate).le("nextStepDate", endDate).findList();
	}

	/**
	 * Get only appts for the specified user within the date range
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Referral> getApptsByIdInRange(Long userId, String startDate, String endDate) {
		return finder.where().eq("user_id", userId).eq("ref_type", "Appt").ge("nextStepDate", startDate).le("nextStepDate", endDate).findList();
	}

	public static List<Referral> getByUserId(Long assignedUserId) {
		return finder.where().eq("user_id", assignedUserId).findList();
	}

	public static List<Referral> getByUserIdNotInFuture(Long assignedUserId, String today) {
		return finder.where().eq("user_id", assignedUserId).disjunction()
			.add(Expr.le("nextStepDate", today))
			.add(Expr.icontains("nextStepDate", "undefined"))
			.findList();
	}

	public static List<Referral> getByClientId(Long id) {
		return finder.where().eq("clientId", id).findList();
	}
	public static List<Referral> getByDate(Date date) {
		//If referral is from that date
		Long endOfDay = date.getTime() + MILLISECONDS_IN_A_DAY;
		return finder.where().between("date_created", date.getTime(), endOfDay).findList();
	}

	/**
	 * Used in nightly cleanup of referrals
	 */
	public static List<Referral> getOpenDeclinedReferrals() {
		return finder.where().eq("ref_type", "Declined").conjunction().add(Expr.eq("status", "OPEN")).findList();
	}

    /**
     Returns the referrals matching the next step date provided

     @param date Date
     @return List of referrals
     */
    public static List<Referral> getByNextStepDate(Date date) {
        return finder.where().eq("next_step_timestamp", date).findList();
    }

	/**
	 Returns a list of referrals whose creator ID is found in the provided list

	 @param creatorIds List of creator IDs
	 @return List of referrals
	 */
	public static List<Referral> getByCreatorIds(List<Long> creatorIds) {
		return finder.where().in("creator_id", creatorIds).findList();
	}

	@Override
	public Long getDateOfLastInteraction() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = f.parse(this.lastEditedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return d.getTime();
	}

	@Override
	public String getDateOfLastInteractionString() {
		return this.lastEditedDate;
	}

	@Override
	public String getRecordType() {
		return "Referral";
	}

	@Override
	public int compareTo(HistoryRecord historyRecord) {
		if (historyRecord.getDateOfLastInteraction() > this.getDateOfLastInteraction()) {
			return 1;
		} else if (historyRecord.getDateOfLastInteraction() < this.getDateOfLastInteraction()) {
			return -1;
		}
		return 0;
	}

	@Override
	public String getRecordStatus() {
		return this.status;
	}

	@Override
	public String getNotes() {
		return this.refNotes;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String getLink() {
		return link;
	}

	public String getNextStepDate() {
		return nextStepDate;
	}

	public void setNextStepDate(String nextStepDate) {
		this.nextStepDate = nextStepDate;
        this.nextStepTimestamp = DateUtilities.normalizeDateString(nextStepDate);
	}

    public Date getNextStepTimestamp() {
        return nextStepTimestamp;
    }

	/**
	 Returns the client name

	 @return Client name
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 Returns the client status

	 @return Client status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 Returns the reason for referral

	 @return Reason for referral
	 */
	public String getReasonForReferral() {
		return reasonForReferral;
	}

	/**
	 Returns the referral notes

	 @return Referral notes
	 */
	public String getRefNotes() {
		return refNotes;
	}

	/**
	 Returns the created date

	 @return Created date
	 */
	public Date getCreatedDate() {
		return new Date(this.dateCreated);
	}
}
