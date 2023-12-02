package account.util;

import java.util.ArrayList;
import java.util.List;

public enum Roles {
    ADMINISTRATOR("ADMINISTRATOR"),
    USER("USER"),
    ACCOUNTANT("ACCOUNTANT"),
    AUDITOR("AUDITOR"); // Added new role

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return "ROLE_" + this.roleName;
    }

    public static class Constants {
        public static final String ADMINISTRATOR = Roles.ADMINISTRATOR.getRoleName();
        public static final String USER = Roles.USER.getRoleName();
        public static final String ACCOUNTANT = Roles.ACCOUNTANT.getRoleName();
        public static final String AUDITOR = Roles.AUDITOR.getRoleName();
    }

    public static List<String> getRolesAsString() {
        List<String> rolesAsString = new ArrayList<>();
        rolesAsString.add(Roles.ADMINISTRATOR.getRoleName());
        rolesAsString.add(Roles.USER.getRoleName());
        rolesAsString.add(Roles.ACCOUNTANT.getRoleName());
        rolesAsString.add(Roles.AUDITOR.getRoleName());
        return rolesAsString;
    }
}
