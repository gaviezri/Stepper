package communication;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Role {
    private String Name;
    private String Description;
    private List<String> assignedFlowNames;

    public Role(String Name, String Description) {
        this.Name = Name;
        this.Description = Description;
        this.assignedFlowNames = new LinkedList<>();
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public List<String> getAssignedFlowNames() {
        return assignedFlowNames;
    }

    public void setAssignedFlowNames(List<String> assignedFlowNames) {
        this.assignedFlowNames = assignedFlowNames;
    }

    public void assignNewFlow(String flowName) {
        assignedFlowNames.add(flowName);
    }

    public static List<Role> createRoleListFromJson(List<Map> rawRolesAsMaps) {
        List<Role> newRoles = new LinkedList<>();
        for (Map rawRoleAsMap : rawRolesAsMaps) {
            try {
                Role role = Utils.GSON_INSTANCE.fromJson(Utils.GSON_INSTANCE.toJson(rawRoleAsMap), Role.class);
                newRoles.add(role);
            } catch (Exception e) {
            }
        }
        return newRoles;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(Name, role.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name, Description);
    }

    public List<String> getFlows() {
        return assignedFlowNames;
    }
}
