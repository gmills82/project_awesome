package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public String email;
    public Long phoneNumber;

    public Date birthDate;
    public String birthDatePretty;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    public List<Income> incomeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    public List<FinancialAsset> assetList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
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
