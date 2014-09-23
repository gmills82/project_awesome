package models;

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
    public Client client;

    public enum AssetType {
        K401, ANNUITY, CASH, CD, GOLD, IRA, PARTNERSHIP, RENTAL_PROP, RETIREMENT_ACCT
    }

    public float totalValue;
    public AssetType realAssetType;

    public String financialInstitute;

    public String description;

    public void setRealAssetType (String assetTypeString) {
        this.realAssetType = AssetType.valueOf(assetTypeString);
    }

    public static List<AssetType> getAllAssetTypes() {
        List<AssetType> all = new ArrayList<AssetType>(Arrays.asList(AssetType.values()));
        return all;
    }

    public static Finder<Long,FinancialAsset> find = new Finder(Long.class, FinancialAsset.class);

    public static List<FinancialAsset> allForClient(Client client) {
        return find.where().eq("clientId", client.id).findList();
    }

}
