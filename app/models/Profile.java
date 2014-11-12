package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * User: grant.mills
 * Date: 8/20/14
 * Time: 1:56 PM
 */
@Entity
public class Profile extends Model {
    @Id
    public long id;
    public long agentId;
    public long clientId;
    public Long createdDate = System.currentTimeMillis();

    public Integer riskTolerance;
    public Integer riskToleranceModifier;
    public Integer adjustedRiskTolerance;
    public String liquidityNeeds;

    public String savingsPlans;
    public String expectedDateOfFundsUse;

    public String advisorRecommendation;
    public String nextSteps;

    public static Finder<Long, Profile> finder = new Finder(Long.class, Profile.class);
    public static Profile getById(Long id) {
        return finder.byId(id);
    }
    public static List<Profile> getByAgentId(Long id) {
        return finder.where().eq("agentId", id).findList();
    }
}
