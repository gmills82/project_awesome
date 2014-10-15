package models;

import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * User: grant.mills
 * Date: 7/28/14
 * Time: 9:23 AM
 */
@Entity
public class User extends Model {

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

    public static void setRoleType(User user, String roleType) {
        user.roleType = Role.valueOf(roleType);
    }

    //Unique name check
    public static Boolean isUserNameTaken(String name) {
        List<User> list = find.where().eq("userName", name).findList();
        if(list.size() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    //Method to salt passwords
    public static String saltPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static Finder<Long, User> find = new Finder(Long.class, User.class);

    //TODO: Refactor into getBy
    public static User getById(Long id) {
        return find.where().eq("id", id).findList().listIterator().next();
    }
    //TODO: Same as above
    public static User getByEmail(String email) {
        return find.where().eq("userName", email).findList().listIterator().next();
    }
    public static List<User> getByPermissionLevel(Role roleType) {
        return find.where().eq("roleType", roleType.getPermissionLevel()).findList();
    }

    public static List<User> getAll() {
        return find.all();
    }

    public static String authenticate(String userName, String password) {
        List<User> userList = find.where().eq("userName", userName).findList();

        if(userList.size() == 0) {
			Logger.debug("No user found");
            return "User not found";
        }
        User possibleUser = userList.listIterator().next();
		Logger.debug("Password: " + password.toString() + " Userpassword: " + possibleUser.password.toString());
		if(BCrypt.checkpw(password, possibleUser.password)) {
            return null;
        }else {
            return "Invalid Login";
        }
    }
}
