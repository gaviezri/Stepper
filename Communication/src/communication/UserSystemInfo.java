package communication;

import java.util.Objects;

public class UserSystemInfo {
    String name;
    Boolean isManager;
//    the roles will be handled bit-wise
    int roles;

    public UserSystemInfo(String name) {
        this.name = name;
        this.isManager = false;
        this.roles = 0;
    }

    public UserSystemInfo(String name, Boolean isManager, int roles) {
        this.name = name;
        this.isManager = isManager;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public Boolean isManager() {
        return isManager;
    }

    public int getRoles() {
        return roles;
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
}
