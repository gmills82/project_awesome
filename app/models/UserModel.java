package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
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
@JsonFilter("password")
public class UserModel extends Model {

    @Id
    public long id;

    @Constraints.Required
    public long userId;

    @Constraints.Email
    @Constraints.Required
    @Column(unique=true)
    public String userName;
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

    /**
     Find a user by ID

     @param id     User ID
     @return User
     */
    public static UserModel getById(Long id) {
        List<UserModel> users = find.where().eq("id", id).findList();
        if (users == null || users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

    /**
     Returns a list of users matching the provided IDs

     @param userIds List of user IDs
     @return List of users
     */
    public static List<UserModel> getByUserIds(List<Long> userIds) {
        return find.where().in("id", userIds).findList();
    }

	public static UserModel getTopParentUser(UserModel user) {
		if(null == user.parent_team_member) {
			return user;
		}else {
			return UserModel.getTopParentUser(user);
		}
	}

	/**
	 * Recursively searches for all children and children of children users of the parent UserModel
	 * @param parent - UserModel which has been assigned as parent UserModel to some child UserModels
	 * @return team - List of child user models
	 */
	public static Set<UserModel> getChildUserModelsByParentAllLevels(UserModel parent) {
		//Unique set of team members to be returned
		Set<UserModel> team = new HashSet<UserModel>();

		//1st level children
		Set<UserModel> firstLevelList = getChildUserModelByParent(parent);
		Iterator<UserModel> iter = firstLevelList.iterator();
		while(iter.hasNext()) {
			//Child
			UserModel child = iter.next();

			//Gather 2nd level children per 1st child
			Set<UserModel> secondLevelListPerChild = getChildUserModelByParent(child);

			//Add each to secondLevelList
			team.addAll(secondLevelListPerChild);

			//Add to return list
			team.add(child);
		}

		return team;
	}

	/**
	 * Get only 1st level children on parent
	 * @param parent - UserModel of parent
	 * @return unique set of 1st level child team members
	 */
	public static Set<UserModel> getChildUserModelByParent(UserModel parent) {
		return find.where().isNotNull("parent_team_member").eq("parent_team_member", parent).findSet();
	}

    public static UserModel getByEmail(String email) {
        return find.where().eq("userName", email).findList().listIterator().next();
    }
    public static List<UserModel> getByPermissionLevel(Role roleType) {
        return find.where().eq("roleType", roleType.getPermissionLevel()).findList();
    }

	public Integer getUserPermissionLevel() {
		return this.roleType.getPermissionLevel();
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

    public List<UserModel> getChildTeamMembers() {
        return childTeamMembers;
    }

    public void setChildTeamMembers(List<UserModel> childTeamMembers) {
        this.childTeamMembers = childTeamMembers;
    }

    /**
     Returns a concatenation of the first and last name

     @return User's full name
     */
    public String getFullName() {
        String firstName = (this.firstName != null) ? this.firstName : "";
        String lastName = (this.lastName != null) ? this.lastName : "";
        return String.format("%s %s", firstName, lastName);
    }
}
