package datastructures.user.component;

import java.util.ArrayList;
import java.util.List;

public enum Privilege {

    ALL_PRIVILEGES, ALTER, DELETE, INDEX, INSERT, SELECT, UPDATE, REFERENCES, UNKNOWN;

    public static Privilege convertToPrivilege(String toConvert) {
        toConvert = toConvert.toUpperCase();
        switch(toConvert) {
            case "ALL_PRIVILEGES":
                return ALL_PRIVILEGES;
            case "ALTER":
                return ALTER;
            case "DELETE":
                return DELETE;
            case "INDEX":
                return INDEX;
            case "INSERT":
                return INSERT;
            case "SELECT":
                return SELECT;
            case "UPDATE":
                return UPDATE;
            case "REFERENCES":
                return REFERENCES;
            default:
                return UNKNOWN;
        }
    }

    /**
     * @return a list of all privileges that can be obtained except for ALL_PRIVILEGES and UNKNOWN
     */
    public static List<Privilege> getAllNonSpecialPrivileges() {

        List<Privilege> privileges = new ArrayList<>();

        privileges.add(Privilege.ALTER);
        privileges.add(Privilege.DELETE);
        privileges.add(Privilege.INDEX);
        privileges.add(Privilege.INSERT);
        privileges.add(Privilege.SELECT);
        privileges.add(Privilege.UPDATE);
        privileges.add(Privilege.REFERENCES);

        return privileges;
    }
}