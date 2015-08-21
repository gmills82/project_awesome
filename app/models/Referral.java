package models;

import com.avaje.ebean.*;
import models.stats.ProducerCallout;
import org.joda.time.DateTime;
import play.Logger;
import play.db.ebean.Model;
import utils.DateUtilities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     Returns a list of referrals whose creator ID is found in the provided list

     @param creatorIds List of creator IDs
     @return List of referrals
     */
    public static List<Referral> getByCreatorIdsBetweenDates(List<Long> creatorIds, Date fromDate, Date toDate) {
        return finder.where()
                .in("creator_id", creatorIds)
                .between("date_created", fromDate.getTime(), toDate.getTime())
                .findList();
    }

	public static Integer getCountBetweenDates(Date fromDate, Date toDate) {
        return getCountBetweenDates(null, fromDate, toDate);
    }

    public static Integer getProductiveCountBetweenDates(Date fromDate, Date toDate) {
        return getProductiveCountBetweenDates(null, fromDate, toDate);
    }

    public static Integer getCountBetweenDates(Long userId, Date fromDate, Date toDate) {
        if (userId != null) {
            return finder.where()
                    .between("date_created", fromDate.getTime(), toDate.getTime())
                    .eq("creator_id", userId)
                    .findRowCount();
        }
        return finder.where().between("date_created", fromDate.getTime(), toDate.getTime()).findRowCount();
    }

    public static Integer getProductiveCountBetweenDates(Long userId, Date fromDate, Date toDate) {
        if (userId != null) {
            return finder.where()
                    .eq("was_productive", true)
                    .eq("creator_id", userId)
                    .between("date_created", fromDate.getTime(), toDate.getTime())
                    .findRowCount();
        }
        return finder.where()
                .eq("was_productive", true)
                .between("date_created", fromDate.getTime(), toDate.getTime())
                .findRowCount();
    }

    public static Referral getTotalsBetweenDates(Date fromDate, Date toDate) {
        String sql = "SELECT SUM(t_ips) AS totalIPS, SUM(t_pc) AS totalPC, SUM(t_insurance) AS totalInsurance FROM referral WHERE date_created BETWEEN :fromDate AND :toDate;";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("fromDate", fromDate.getTime())
                .setParameter("toDate", toDate.getTime())
                .findList();

        SqlRow sqlRow = sqlRows.get(0);

        Referral referral = new Referral();
        referral.settInsurance(sqlRow.getInteger("totalInsurance"));
        referral.settIps(sqlRow.getInteger("totalIPS"));
        referral.settPc(sqlRow.getInteger("totalPC"));

        return referral;
    }

    public static ProducerCallout getByMostTotalClients(List<Long> creatorIds, Date fromDate, Date toDate) {

        List<SqlRow> sqlRows;

        if (creatorIds == null) {

            String sql = "SELECT referral.creator_id, COUNT(referral.*) FROM referral LEFT JOIN user_model ON referral.creator_id = user_model.id WHERE referral.date_created BETWEEN :fromDate AND :toDate AND user_model.role_type = 2 GROUP BY referral.creator_id ORDER BY COUNT(referral.*) DESC;";

            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .findList();
        } else {

            String sql = "SELECT referral.creator_id, COUNT(referral.*) FROM referral LEFT JOIN user_model ON referral.creator_id = user_model.id WHERE referral.date_created BETWEEN :fromDate AND :toDate AND user_model.role_type = 2 AND referral.creator_id IN (:creatorIds) GROUP BY referral.creator_id ORDER BY COUNT(referral.*) DESC;";

            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .setParameter("creatorIds", creatorIds)
                    .findList();

        }

        if (sqlRows == null || sqlRows.size() == 0) {
            return null;
        }
        SqlRow sqlRow = sqlRows.get(0);

        // Parse out the fields we care about
        Long userId = sqlRow.getLong("creator_id");
        Integer count = sqlRow.getInteger("count");

        // Look up the user and remove the child team members. They're not needed for this call.
        UserModel user = UserModel.getById(userId);
        if (user == null) {
            return null;
        }
        user.setChildTeamMembers(null);

        // Populate the return entity and send it back
        ProducerCallout callout = new ProducerCallout();
        callout.setUser(user);
        callout.setCallout(count.floatValue());
        if (user.parent_team_member != null) {
            callout.setSupervisor(UserModel.getById(user.parent_team_member.id));
        }
        return callout;
    }

    public static ProducerCallout getByMostProductiveClients(List<Long> creatorIds, Date fromDate, Date toDate) {

        List<SqlRow> sqlRows;

        if (creatorIds == null) {
            String sql = "SELECT creator_id, COUNT(*) FROM referral WHERE was_productive = TRUE AND date_created BETWEEN :fromDate AND :toDate GROUP BY creator_id ORDER BY COUNT(*) DESC;";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .findList();
        } else {
            String sql = "SELECT creator_id, COUNT(*) FROM referral WHERE was_productive = TRUE AND date_created BETWEEN :fromDate AND :toDate AND creator_id IN (:creatorIds) GROUP BY creator_id ORDER BY COUNT(*) DESC;";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .setParameter("creatorIds", creatorIds)
                    .findList();
        }

        if (sqlRows == null || sqlRows.size() == 0) {
            return null;
        }

        SqlRow sqlRow = sqlRows.get(0);

        // Parse out the fields we care about
        Long userId = sqlRow.getLong("creator_id");
        Integer count = sqlRow.getInteger("count");

        // Look up the user and remove the child team members. They're not needed for this call.
        UserModel user = UserModel.getById(userId);
        if (user == null) {
            return null;
        }
        user.setChildTeamMembers(null);

        // Populate the return entity and send it back
        ProducerCallout callout = new ProducerCallout();
        callout.setUser(user);
        callout.setCallout(count.floatValue());
        if (user.parent_team_member != null) {
            callout.setSupervisor(UserModel.getById(user.parent_team_member.id));
        }
        return callout;
    }

    public static ProducerCallout getByMostProductiveClientsPercentage(List<Long> creatorIds, Date fromDate, Date toDate) {

        List<SqlRow> sqlRows;

        if (creatorIds == null) {
            String sql = "SELECT creator_id, COUNT(*) FROM referral WHERE was_productive = TRUE AND date_created BETWEEN :fromDate AND :toDate GROUP BY creator_id ORDER BY COUNT(*) DESC;";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .findList();
        } else {
            String sql = "SELECT creator_id, COUNT(*) FROM referral WHERE was_productive = TRUE AND date_created BETWEEN :fromDate AND :toDate AND creator_id IN (:creatorIds) GROUP BY creator_id ORDER BY COUNT(*) DESC;";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("fromDate", fromDate.getTime())
                    .setParameter("toDate", toDate.getTime())
                    .setParameter("creatorIds", creatorIds)
                    .findList();
        }



        if (sqlRows == null || sqlRows.size() == 0) {
            return null;
        }

        // Extract the user IDs from the list
        List<ProducerCallout> callouts = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (SqlRow row : sqlRows) {
            userIds.add(row.getLong("creator_id"));
        }

        // Look up the users from the extracted user IDs and map them for easier lookup
        List<UserModel> users = UserModel.getByUserIds(userIds);
        Map<Long, UserModel> userModelMap = new HashMap<>();
        for (UserModel user : users) {
            userModelMap.put(user.id, user);
        }

        Map<Long, Integer> totalReferalMap = getTotalReferralsByUserIds(userIds);

        ProducerCallout callout = new ProducerCallout();
        callout.setCalloutType(ProducerCallout.CalloutType.PERCENTACE);

        // Loop over the returned rows one more time to pull the correct user from the map and populate the return data.
        // If the user wasn't found in the map, we'll assume that the user doesn't actually exist and some sort of voodoo
        // happened and ignore it.
        for (SqlRow row : sqlRows) {
            Long userId = row.getLong("creator_id");
            Integer count = row.getInteger("count");
            if (userModelMap.get(userId) != null && totalReferalMap.get(userId) != null) {
                Float percentage = (count.floatValue() / totalReferalMap.get(userId).floatValue()) * 100;

                // If the return callout hasn't been set yet, or the calculated percentage is higher than the currently
                // set callout percentage, overwrite the return callout
                if (callout.getUser() == null || callout.getCallout() == null || percentage > callout.getCallout()) {
                    callout.setCallout(percentage);
                    UserModel user = userModelMap.get(userId);
                    user.setChildTeamMembers(null);
                    callout.setUser(user);
                }
            }
        }
        if (callout.getUser() != null && callout.getUser().parent_team_member != null) {
            callout.setSupervisor(UserModel.getById(callout.getUser().parent_team_member.id));
        }
        return callout;
    }

    private static Map<Long, Integer> getTotalReferralsByUserIds(List<Long> userIds) {

        // Execute the SQL
        String sql = "SELECT creator_id, COUNT(*) FROM referral WHERE creator_id IN (:userIds) GROUP BY creator_id ORDER BY COUNT(*) DESC;";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("userIds", userIds)
                .findList();

        Map<Long, Integer> dataMap = new HashMap<>();
        for (SqlRow row : sqlRows) {
            Long userId = row.getLong("creator_id");
            Integer count = row.getInteger("count");
            dataMap.put(userId, count);
        }
        return dataMap;
    }

    public static List<Referral> getByUserIdsBetweenDates(List<Long> userIds, Date fromDate, Date toDate) {
        return finder.where()
                .in("user_id", userIds)
                .between("date_created", fromDate.getTime(), toDate.getTime())
                .findList();
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

    public Integer gettInsurance() {
        return tInsurance;
    }

    public void settInsurance(Integer tInsurance) {
        this.tInsurance = tInsurance;
    }

    public Integer gettPc() {
        return tPc;
    }

    public void settPc(Integer tPc) {
        this.tPc = tPc;
    }

    public Integer gettIps() {
        return tIps;
    }

    public void settIps(Integer tIps) {
        this.tIps = tIps;
    }

	@Override
	public String getCreatorName() {
		UserModel creator = UserModel.getById(this.creatorId);
		return creator.getFullName();
	}
}
