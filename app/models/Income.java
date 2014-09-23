package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: grant.mills
 * Date: 8/22/14
 * Time: 10:34 AM
 */
@Entity
public class Income extends Model {
    @Id
    public long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client", referencedColumnName = "id")
    public Client client;

    public Long assetId;

    public float value;

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
    public enum IncomeType {
        SALARY, INVESTMENT, RENTAL_PROPERTY, BUSINESS, INTELLECTUAL_PROPERTY
    }
    public Frequency frequency;
    public IncomeType incomeType;

    public String description;
}
