package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
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
	@JsonBackReference
    public Client client;

    public enum DebtType {
        MORTGAGE, CC, STUDENT_LOANS, AUTO
    }
	public enum PaymentFrequency {
		ANNUALLY(1), MONTHLY(12), BIWEEKLY(26), WEEKLY(52), DAILY(365);
		private Integer valueMultiplier;
		PaymentFrequency(Integer valueMultiplier) {
			this.valueMultiplier = valueMultiplier;
		}
		public Integer getValueMultiplier() {
			return valueMultiplier;
		}
	}

    public DebtType realDebtType;
    public Float totalOwed;
	public String frequencyStr;
	public Integer frequency;
	public Float recurringAmount;

    public String description;
    public String financialInstitute;

	//Getters and Setters
    public void setRealDebtType(String debtTypeString) {
        this.realDebtType = DebtType.valueOf(debtTypeString);
    }
	public void setFrequency(String frequencyStr) {
		PaymentFrequency freq = PaymentFrequency.valueOf(frequencyStr);
		if(null != freq) {
			this.frequency = freq.getValueMultiplier();
		}
	}

	//Util methods
	public static List<DebtType> getAllDebtTypes() {
		List<DebtType> all = new ArrayList<DebtType>(Arrays.asList(DebtType.values()));
		return all;
	}

    public static Finder<Long, Debt> find = new Finder(Long.class, Debt.class);

    public static List<Debt> allForClient(Client client) {
        return find.where().eq("clientId", client.id).findList();
    }
}
