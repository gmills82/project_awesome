package models;

import java.util.ArrayList;
import java.util.List;

/**
 User roles are based on a permission level, with the lower number having more permission. They're incremented in sets of
 100 to allow for sub-roles.

 User: justin.podzimek
 Date: 9/29/15
 */
public enum UserRole {

    // EFA Roles
    FA(0, null, "EFA", "an"),
    EFA_ASSISTANT(1, UserRole.FA, "EFA Assistant", "an"),
    SUB_EFA(10, UserRole.FA, "Sub Producer", "a"),

    // Agent Roles
    AGENT(100, null, "Agent", "an"),

    // Producer/LSP Roles
    PRODUCER(200, null, "LSP", "an");

    private Integer permissionLevel;
    private UserRole group;
    private String declaration;
    private String article;

    /**
     Constructor with a provided permission level, group, declaration, and article

     @param permissionLevel Permission level
     @param group Group
     @param declaration Role declaration
     @param article Article
     */
    UserRole(Integer permissionLevel, UserRole group, String declaration, String article) {
        this.permissionLevel = permissionLevel;
        this.group = group;
        this.declaration = declaration;
        this.article = article;
    }

    /**
     Returns the permission level

     @return Permission level
     */
    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    /**
     Returns the group

     @return Group
     */
    public UserRole getGroup() {
        return group;
    }

    /**
     Returns the user role declaration

     @return Declaration
     */
    public String getDeclaration() {
        return declaration;
    }

    /**
     Returns the article

     @return Article
     */
    public String getArticle() {
        return article;
    }

    /**
     Returns the user role for the provided permission level

     @param permissionLevel Permission level
     @return User role
     */
    public static UserRole getUserRoleForPermissionLevel(Integer permissionLevel) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getPermissionLevel().equals(permissionLevel)) {
                return userRole;
            }
        }
        return null;
    }

    /**
     Returns whether or not the user role is permissible by the provided role

     @param userRole User role to test permissions against
     @return True if permissible, false otherwise
     */
    public boolean isPassingPermissionLevel(UserRole userRole) {
        return this.getPermissionLevel() <= userRole.getPermissionLevel();
    }

    /**
     Returns whether or not the user role is permissible by the provided permission level

     @param permissionLevel Permission level to test
     @return True if permissible, false otherwise
     */
    public boolean isPassingPermissionLevel(Integer permissionLevel) {
        UserRole userRole = UserRole.getUserRoleForPermissionLevel(permissionLevel);
        return isPassingPermissionLevel(userRole);
    }

    /**
     Returns whether or not the user role is of the provided group

     @param userRole User role group
     @return True if part of group, false otherwise
     */
    public boolean isGroupRole(UserRole userRole) {
        if (this.getGroup() == null) {
            return this == userRole;
        }
        return this.getGroup() == userRole;
    }

    /**
     Returns all roles that are children of the current role with the option to include the sub-role or not

     @param includeSubRole Whether or not to include the sub role
     @return List of children roles
     */
    public List<UserRole> getChildRoles(Boolean includeSubRole) {
        List<UserRole> roles = new ArrayList<>();
        Integer childPermissionLevel = getPermissionLevelForChildRole();
        for (UserRole role : UserRole.values()) {

            // If we're including sub-roles in the children, any role with a permission level greater than the currently
            // set level is assumed a child.
            if (includeSubRole) {
                if (role.getPermissionLevel() > this.getPermissionLevel()) {
                    roles.add(role);
                }
            }
            // If no sub-roles are desired, we'll first gather the next full permission level role and verify that the
            // current iterated role has a permission level greater than or equal to that as well.
            else {
                if (role.getPermissionLevel() > this.getPermissionLevel() && role.getPermissionLevel() >= childPermissionLevel) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    /**
     Returns the immediate child role for the current role. With the introduction of sub roles, we can either get back
     the highest sub role for the current role, or the next full role.

     @param includeSubRole Whether or not to include the sub role
     @return Child role
     */
    public UserRole getChildRole(Boolean includeSubRole) {
        if (includeSubRole) {
            for (UserRole role : UserRole.values()) {
                if (role.getPermissionLevel() > this.getPermissionLevel()) {
                    return role;
                }
            }
            return null;
        }
        Integer childPermissionLevel = getPermissionLevelForChildRole();
        return UserRole.getUserRoleForPermissionLevel(childPermissionLevel);
    }

    /**
     Returns the permission level for the next full set of roles

     @return Permission level
     */
    private Integer getPermissionLevelForChildRole() {
        return (int) (100 * (Math.floor((this.getPermissionLevel() + 50) / 100))) + 100;
    }
}
