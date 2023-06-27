package dto.roles.map;

import communication.Role;
import dto.AbstractDTO;

import java.util.Map;

public class RolesMapDTO extends AbstractDTO {
    private Map<String, Role> rolesMap;

    public RolesMapDTO(Map<String, Role> rolesMap) {
        this.rolesMap = rolesMap;
    }
    public Map<String, Role> getRolesMap() {
        return rolesMap;
    }
}
