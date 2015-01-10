package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.h2.expression.ExpressionList;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

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


	/**
	 * Recursively searches for all children and children of children users of the parent UserModel
	 * @param parent - UserModel which has been assigned as parent UserModel to some child UserModels
	 * @return team - List of child user models
	 */
	public static Set<UserModel> getChildUserModelsByParentAllLevels(UserModel parent) {
		//Unique set of team members to be returned
		Set<UserModel> team = new HashSet<UserModel>();
		Logger.debug("Inside getChildUserModelsByParentAllLevels");

		//1st level children
		Set<UserModel> firstLevelList = getChildUserModelByParent(parent);
		Logger.debug("Got 1st level children");
		Iterator<UserModel> iter = firstLevelList.iterator();
		Logger.debug("Have iter");
		while(iter.hasNext()) {
			//Child
			UserModel child = iter.next();
			Logger.debug("Have first 2nd level child");

			//Gather 2nd level children per 1st child
			Set<UserModel> secondLevelListPerChild = getChildUserModelByParent(child);

			//Add each to secondLevelList
			team.addAll(secondLevelListPerChild);

			//Add to return list
			team.add(child);
		}

		Logger.debug("Total team members: " + team.size());
		return team;
	}

	/**
	 * Get only 1st level children on parent
	 * @param parent - UserModel of parent
	 * @return unique set of 1st level child team members
	 */
	public static Set<UserModel> getChildUserModelByParent(UserModel parent) {
		Logger.debug("Parent with id: " + parent.id);
		return find.where().isNotNull("parent_team_member").eq("parent_team_member", parent).findSet();
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
            return "User not found";
        }
        UserModel possibleUser = userList.listIterator().next();
		if(BCrypt.checkpw(password, possibleUser.password)) {
            return null;
        }else {
            return "Invalid Login";
        }
    }
}
