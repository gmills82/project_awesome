package models;

/**
 User roles are based on a permission level, with the lower number having more permission. They're incremented in sets of
 100 to allow for sub-roles.

 User: justin.podzimek
 Date: 9/29/15
 */
public enum UserRole {

    FA(0, "EFA"),
    EFA_ASSISTANT(1, "EFA Assistant"),
    AGENT(100, "Agent"),
    PRODUCER(200, "LSP");

    private Integer permissionLevel;
    private String declaration;

    /**
     Constructor with a provided permission level

     @param permissionLevel Permission level
     */
    UserRole(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    /**
     Constructor with a provided permission level

     @param permissionLevel Permission level
     @param declaration Role declaration
     */
    UserRole(Integer permissionLevel, String declaration) {
        this.permissionLevel = permissionLevel;
        this.declaration = declaration;
    }

    /**
     Returns the permission level

     @return Permission level
     */
    public Integer getPermissionLevel() {
        return permissionLevel;
    }

    /**
     Returns the user role declaration

     @return Declaration
     */
    public String getDeclaration() {
        return declaration;
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
