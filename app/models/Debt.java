package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * User: grant.mills
 * Date: 7/18/14
 * Time: 9:12 AM
 */
@Entity
public class Debt extends Model {
    @Id
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client", referencedColumnName = "id")
    public Client client;

    public enum DebtType {
        MORTGAGE, CC, STUDENT_LOANS, AUTO
    }
    public enum Frequency {
        ANNUALLY(1), MONTHLY(12), BIWEEKLY(26), WEEKLY(52), DAILY(365);
        private Integer valueMultiplier;
        Frequency(Integer valueMultiplier) {
            this.valueMultiplier = valueMultiplier;
        }
        public Integer getValueMultiplier() {
            return valueMultiplier;
        }
    }

    public DebtType debtType;
    public Float value;
    public Frequency frequency;
    public Float totalOwed;

    public String description;
    public String financialInstitute;

    public void setRealDebtType(String debtTypeString) {
        String adj = debtTypeString.replaceAll("debtTypeString_", "");
        this.debtType = DebtType.valueOf(debtTypeString);
    }

    public static Finder<Long, Debt> find = new Finder(Long.class, Debt.class);

    public static List<Debt> allForClient(Client client) {
        return find.where().eq("clientId", client.id).findList();
    }
}
