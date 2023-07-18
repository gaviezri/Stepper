package dto.user.roles;
import communication.Role;
import dto.AbstractDTO;

import java.util.ArrayList;
import java.util.List;

public class RolesDTO extends AbstractDTO {
    List<Role> roles;

    public RolesDTO(List<Role> roles) {
        this.roles = roles;
    }

    public RolesDTO() {
        this.roles = new ArrayList<>();
    }

    public List<Role> getRoles() {
        return roles;
    }
}
