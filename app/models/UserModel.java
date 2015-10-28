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

    @Column(name = "role_type")
    private Integer roleType;

    @Transient
    private UserRole role;

    private Integer groupId;

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

    /**
     * Returns all users matching the provided role
     *
     * @param role Role to match
     * @return List of users
     */
    public static List<UserModel> getByUserRole(UserRole role) {
        return find.where().eq("role_type", role.getPermissionLevel()).findList();
    }

    /**
     Returns all users matching the provided group ID and roles

     @param groupId Group ID
     @param roles Roles
     @return List of users
     */
    public static List<UserModel> getByGroupAndRole(Integer groupId, UserRole... roles) {
        List<Integer> permissionLevels = new ArrayList<>();
        for (UserRole role : roles) {
            permissionLevels.add(role.getPermissionLevel());
        }
        return find.where()
                .eq("group_id", groupId)
                .in("role_type", permissionLevels)
                .findList();
    }

    /**
     Returns all team members that are children of the provided parent in the same group

     @param parent Parent team member
     @return Children team members
     */
	public static Set<UserModel> getChildUserModelsByParentAllLevels(UserModel parent) {

        // If the parent has a group ID, we can use that to look up all children. Otherwise, we need to get the children
        // and (theoretically, it's not actually happening like this) recursively look up *their* children to get the
        // full list of users.
        if (parent.getGroupId() != null && parent.getRole() != null) {
            Logger.info("Looking up children for team member {} by group ID...", parent.getId());
            List<UserRole> roles = parent.getRole().getChildRoles(true);
            UserRole[] userRoles = roles.toArray(new UserRole[roles.size()]);
            return new HashSet<>(UserModel.getByGroupAndRole(parent.getGroupId(), userRoles));
        }

        Logger.info("Looking up children for team member {} iteratively...", parent.getId());

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

        // If there's a group ID on the parent, use it to look up the children for a more specific set. In the grand
        // scheme of things, this won't usually make a difference, but it's good for assurance that the child is part
        // of the same group.
        if (parent.getGroupId() != null) {
            return find.where()
                    .isNotNull("parent_team_member")
                    .eq("parent_team_member", parent)
                    .eq("group_id", parent.getGroupId())
                    .findSet();
        }
		return find.where().isNotNull("parent_team_member").eq("parent_team_member", parent).findSet();
	}

    public static UserModel getByEmail(String email) {
        return find.where().eq("userName", email).findList().listIterator().next();
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

    /*************************************************************
     GETTERS & SETTERS
     ************************************************************/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public UserRole getRole() {
        if (role == null) {
            role = UserRole.getUserRoleForPermissionLevel(this.roleType);
        }
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
