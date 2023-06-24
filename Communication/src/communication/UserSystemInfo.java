package communication;

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

    public Boolean getManager() {
        return isManager;
    }

    public int getRoles() {
        return roles;
    }
}
