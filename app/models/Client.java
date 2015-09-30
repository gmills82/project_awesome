package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.common.BeanSet;
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
    public String name;

    @Constraints.Email
    public String userName;
    public String phoneNumber;
	public String address1;
	public String city;
	public String state;
	public String zipcode;

    public Long birthDate;
    public String birthDatePretty;

	public String goalsString;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@JsonManagedReference
    public List<FinancialAsset> assetList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@JsonManagedReference
    public List<Debt> debtList;

    public Long group;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

	public static Set<Client> query(String queryString) {
		//Break string up by word
		String[] values = queryString.split(" ");
		Set<Client> clientList = new HashSet<Client>();
		for (String value: values) {
			clientList.addAll(find.where().disjunction()
				.add(Expr.icontains("name", value))
				.add(Expr.icontains("phone_number", value))
				.add(Expr.icontains("address1", value))
				.add(Expr.icontains("zipcode", value))
				.findSet());
		}
		return clientList;
	}

    public static List<Client> all() {
        return find.all();
    }

    public static Client getById(long id) {
        return find.byId(id);
    }

    /**
     Returns the list of clients matching the provided IDs

     @param ids Client ID list
     @return Client list
     */
    public static List<Client> getByIds(List<Long> ids) {
        return find.where().in("id", ids).findList();
    }

    /**
     Returns the last client that was added

     @return Last client
     */
    public static Client getLastInsertedClient() {
        return find.orderBy("id DESC").setMaxRows(1).findUnique();
    }

    /**
     Removes a client with the provided ID

     @param id Client ID
     */
    public static void removeById(Long id) {
        Client client = getById(id);
        if (client != null) {
            client.delete();
        }
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }
}
