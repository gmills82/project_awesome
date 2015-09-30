package models;

/**
 User roles are based on a permission level, with the lower number having more permission. They're incremented in sets of
 100 to allow for sub-roles.

 User: justin.podzimek
 Date: 9/29/15
 */
public enum UserRole {

    FA(0),
    ASSISTANT(1),
    AGENT(100),
    PRODUCER(200);

    private Integer permissionLevel;

    /**
     Constructor with a provided permission level

     @param permissionLevel Permission level
     */
    UserRole(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    /**
     Returns the permission level

     @return Permission level
     */
    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    /**
     Sets the permission level

     @param permissionLevel Permission level
     */
    public void setPermissionLevel(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
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
}
