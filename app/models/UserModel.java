package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:23 AM
 */
@Entity
public class UserModel extends Model {

    @Id
    public long id;

    @Constraints.Required
    public long userId;

    @Constraints.Email
    @Constraints.Required
    @Column(unique=true)
    public String userName;
    @Constraints.Required
    public String password;

	@Constraints.Required
	public String firstName;

	@Constraints.Required
	public String lastName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent_team_member")
	@JsonManagedReference
	public List<UserModel> childTeamMembers;

	@ManyToOne(optional = true)
	@JoinColumn(name="parent_team_member", referencedColumnName = "id")
	@JsonBackReference
	public UserModel parent_team_member;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user_id")
	@JsonManagedReference
	public List<Referral> referrals;

    public enum Role {
        FA(0), Agent(1), Producer(2);
        private Integer permissionLevel;
        Role(Integer permissionLevel) {
            this.permissionLevel = permissionLevel;
        }
        public Integer getPermissionLevel() {
            return permissionLevel;
        }
    }

    public Role roleType;

    public static void setRoleType(UserModel user, String roleType) {
        user.roleType = Role.valueOf(roleType);
    }
    public static void setRoleType(UserModel user, Integer roleTypeNum) {
		switch(roleTypeNum) {
			case 0: user.roleType = Role.FA;
				break;
			case 1: user.roleType = Role.Agent;
				break;
			case 2: user.roleType = Role.Producer;
				break;
			default:
				user.roleType = null;
				break;
		}
    }

    //Unique name check
    public static Boolean isUserNameTaken(String name) {
        List<UserModel> list = find.where().eq("userName", name).findList();
        if(list.size() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    //Method to salt passwords
    public static String saltPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static Finder<Long, UserModel> find = new Finder(Long.class, UserModel.class);

    public static UserModel getById(Long id) {
        return find.where().eq("id", id).findList().listIterator().next();
    }

    public static UserModel getByEmail(String email) {
        return find.where().eq("userName", email).findList().listIterator().next();
    }
    public static List<UserModel> getByPermissionLevel(Role roleType) {
        return find.where().eq("roleType", roleType.getPermissionLevel()).findList();
    }

    public static List<UserModel> getAll() {
        return find.all();
    }

    public static String authenticate(String userName, String password) {
        List<UserModel> userList = find.where().eq("userName", userName).findList();

        if(userList.size() == 0) {
			Logger.debug("No user found");
            return "User not found";
        }
        UserModel possibleUser = userList.listIterator().next();
		Logger.debug("Password: " + password.toString() + " Userpassword: " + possibleUser.password.toString());
		if(BCrypt.checkpw(password, possibleUser.password)) {
            return null;
        }else {
            return "Invalid Login";
        }
    }
}
