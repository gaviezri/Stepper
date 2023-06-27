package communication;

import java.io.IOException;
import java.util.*;

public class Role {
    public static class RoleManager{
        private Map<Integer,Role> rolesMap = new LinkedHashMap<>();

        public void setRolesMap(Map<Integer, Role> rolesMap) {
            this.rolesMap = rolesMap;
        }

        public Map<Integer,Role> getRolesMap() {
            return rolesMap;
        }

        public void addRole(Role role){
            rolesMap.put(role.getId(),role);
        }

         synchronized public List<Role> getRolesListFromCumulativeRoleValue(Integer cumulativeRoleValue){
            List<Role> rolesList = new LinkedList<>();
            for (Map.Entry<Integer,Role> entry : rolesMap.entrySet()){
                if ((cumulativeRoleValue & entry.getKey()) != 0){
                    rolesList.add(entry.getValue());
                }
            }
            return rolesList;
        }

        public void addRoles(List<Role> newRole) {
            for (Role role : newRole) {
                rolesMap.put(role.getId(), role);
            }
        }
    }



    private static int bitwiseId = 1;
    private static Map<Integer,Role> rolesMap;
    private String Name;
    private String Description;
    private List<String> assignedFlowNames;
    private int id;



    public Role(String Name, String Description) {
        this.Name = Name;
        this.Description = Description;
        this.assignedFlowNames = new LinkedList<>();
        this.id = bitwiseId;
        bitwiseId *=2;
    }

    public Role(String Name, String Description, List<String> assignedFlowNames, int id) {
        this.Name = Name;
        this.Description = Description;
        this.assignedFlowNames = assignedFlowNames;
        this.id = id;
    }

    private Integer getId() {
        return id;
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
        if (!assignedFlowNames.contains(flowName)) {
            assignedFlowNames.add(flowName);
        }
    }


    public List<String> getFlows() {
        return assignedFlowNames;
    }

    public void unassignOldFlow(String flowName) {
        assignedFlowNames.remove(flowName);
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


}
