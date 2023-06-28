package communication;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class UserSystemInfo {
    String name;
    Boolean isManager;
//    the roles will be handled bit-wise
    List<Role> assignedRoles;

    public UserSystemInfo(String name) {
        this.name = name;
        this.isManager = false;
        assignedRoles = new LinkedList<>();
    }

    public UserSystemInfo(String name, Boolean isManager, List<Role> roles) {
        this.name = name;
        this.isManager = isManager;
        assignedRoles = roles;
    }

    public String getName() {
        return name;
    }

    public Boolean isManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
    }

    public List<Role> getRoles() {
        return assignedRoles;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSystemInfo that = (UserSystemInfo) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void assignNewRole(Role x) {
        if (!assignedRoles.contains(x)) {
            assignedRoles.add(x);
        }
    }

    public void unAssignNewRole(Role x) {
        assignedRoles.remove(x);
    }

    public void updateRoles(List<Role> allRoles) {
        for (Role role : assignedRoles) {
            if (!allRoles.contains(role)) {
                assignedRoles.remove(role);
            } else {
                assignedRoles.set(assignedRoles.indexOf(role), allRoles.get(allRoles.indexOf(role)));
            }
        }

    }
}
