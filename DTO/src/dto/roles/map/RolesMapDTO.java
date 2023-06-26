package dto.roles.map;

import communication.Role;
import dto.AbstractDTO;

import java.util.Map;

public class RolesMapDTO extends AbstractDTO {
    private Map<Integer, Role> rolesMap;

    public RolesMapDTO(Map<Integer, Role> rolesMap) {
        this.rolesMap = rolesMap;
    }
    public Map<Integer, Role> getRolesMap() {
        return rolesMap;
    }
}
