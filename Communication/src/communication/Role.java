package communication;

import java.util.*;

public class Role {
    public static class RoleManager{
        private Map<String,Role> rolesMap = new LinkedHashMap<>();

        public void setRolesMap(Map<String, Role> rolesMap) {
            this.rolesMap = rolesMap;
        }

        public Map<String,Role> getRolesMap() {
            return rolesMap;
        }

        public void addRole(Role role){
            rolesMap.put(role.getName(),role);
        }

         synchronized public List<Role> getRolesListFromStringsValue(List<String> roleNames){
            List<Role> rolesList = new LinkedList<>();
            for (String roleName : roleNames) {
                rolesList.add(rolesMap.get(roleName));
            }
            return rolesList;
        }

        public void addRoles(List<Role> newRole) {
            for (Role role : newRole) {
                rolesMap.put(role.getName(), role);
            }
        }
    }


    private String Name;
    private String Description;
    private List<String> assignedFlowNames;




    public Role(String Name, String Description) {
        this.Name = Name;
        this.Description = Description;
        this.assignedFlowNames = new LinkedList<>();
    }

    public Role(String Name, String Description, List<String> assignedFlowNames, int id) {
        this.Name = Name;
        this.Description = Description;
        this.assignedFlowNames = assignedFlowNames;
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
    public String toString() {
        return Name;
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
        return Objects.hash(Name);
    }


}
