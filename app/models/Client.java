package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import javax.validation.Constraint;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * User: grant.mills
 * Date: 7/10/14
 * Time: 9:10 AM
 */
@Entity
public class Client extends Model {

    @Id
    public long id;

    public String acctNumber;

    @Constraints.Required
    public String name;

    @Constraints.Email
    public String userName;
    public Long phoneNumber;

    public Long birthDate;
    public String birthDatePretty;
	public String refNotes;

	public String goalsString;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@JsonManagedReference
    public List<Income> incomeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@JsonManagedReference
    public List<FinancialAsset> assetList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@JsonManagedReference
    public List<Debt> debtList;

    public static void addIncome(Client client, Income income) {
        client.incomeList.add(income);
    }
    public static void removeIncome(Client client, Income income) {
        client.incomeList.remove(income);
        income.delete();
    }

    public static void addDebt(Client client, Debt debt) {
        client.debtList.add(debt);
    }
    public static void removeDebt(Client client, Debt debt) {
        client.debtList.remove(debt);
        debt.delete();
    }


    public static void addAsset(Client client, FinancialAsset asset) {
        client.assetList.add(asset);
    }
    public static void removeAsset(Client client, FinancialAsset asset) {
        client.assetList.remove(asset);
        asset.delete();
    }

    public static Finder<Long,Client> find = new Finder(
        Long.class, Client.class
    );

    public static List<Client> all() {
        return find.all();
    }

    public static Client getById(long id) {
        return find.byId(id);
    }

}
