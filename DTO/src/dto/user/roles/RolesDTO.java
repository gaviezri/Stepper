package dto.user.roles;

import communication.Role;

import java.util.List;

public class RolesDTO {
    List<Role> roles;

    public RolesDTO(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
