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
 * Date: 7/15/14
 * Time: 9:03 AM
 */
@Entity
public class FinancialAsset extends Model {
    @Id
    public long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="client", referencedColumnName = "id")
	@JsonBackReference
    public Client client;

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

    public enum AssetType {
        K401, ANNUITY, CASH, CD, GOLD, IRA, PARTNERSHIP, RENTAL_PROP, RETIREMENT_ACCT
    }

    public float totalValue;
    public AssetType realAssetType;
	public String frequencyStr;
	public Integer frequency;
	public Float recurringAmount;
    public String financialInstitute;
    public String description;

	//Getters and Setters
    public void setRealAssetType (String assetTypeString) {
        this.realAssetType = AssetType.valueOf(assetTypeString);
    }
	public void setFrequency(String frequencyStr) {
		PaymentFrequency freq = PaymentFrequency.valueOf(frequencyStr);
		if(null != freq) {
			this.frequency = freq.getValueMultiplier();
		}
	}

	//Util
    public static List<AssetType> getAllAssetTypes() {
        List<AssetType> all = new ArrayList<AssetType>(Arrays.asList(AssetType.values()));
        return all;
    }

    public static Finder<Long,FinancialAsset> find = new Finder(Long.class, FinancialAsset.class);

    public static List<FinancialAsset> allForClient(Client client) {
        return find.where().eq("clientId", client.id).findList();
    }

}
