package communication;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class UserSystemInfo {
    String name;
    Boolean isManager;
//    the roles will be handled bit-wise
    int roles;
    List<String> assignedRoles;

    public UserSystemInfo(String name) {
        this.name = name;
        this.isManager = false;
        assignedRoles = new LinkedList<>();
    }

    public UserSystemInfo(String name, Boolean isManager, List<String> roles) {
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

    public List<String> getRoles() {
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
        assignedRoles.add(x.getName());
    }

    public void unAssignNewRole(Role x) {
        assignedRoles.remove(x.getName());
    }
}
