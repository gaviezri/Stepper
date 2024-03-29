package GUI.body;

import GUI.app.AppController;
import GUI.body.history.HistoryController;
import communication.Role;
import GUI.body.roles.RolesController;
import GUI.body.statistics.StatisticsController;
import GUI.body.users.management.UsersManagementController;
import communication.UserSystemInfo;
import dto.flow.FlowNamesDTO;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.statistics.StatisticsDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BodyController {
    public static final int USERS_MANAGEMENT_TAB = 0;
    public static final int ROLES_MANAGEMENT_TAB = 1;
    public static final int FLOW_HIST_TAB = 2;
    public static final int FLOW_STAT_TAB = 3;
    private AppController mainController;

    @FXML private UsersManagementController usersManagementTabComponentController;
    @FXML private RolesController rolesManagementTabComponentController;
    @FXML private StatisticsController flowStatComponentController;
    @FXML private HistoryController flowHistoryComponentController;

    @FXML private TabPane mainTabPane;
    @FXML private Tab usersManagementTab;
    @FXML private AnchorPane usersManagementTabComponent;
    @FXML private Tab rolesManagementTab;
    @FXML private AnchorPane rolesManagementTabComponent;
    @FXML private Tab flowStatTab;
    @FXML private TabPane flowStatComponent;
    @FXML private Tab flowHistoTab;
    @FXML private  AnchorPane flowHistoryComponent;

    private Role.RoleManager roleManager;
    private boolean fetchedRolesMap;

    public void setActiveTab(int activeTab) {
        this.mainTabPane.getSelectionModel().select(activeTab);
    }

    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        fetchedRolesMap = false;
        roleManager = new Role.RoleManager();
        flowHistoTab.setDisable(true);
        flowStatTab.setDisable(true);
        flowHistoryComponentController.setBodyController(this);
        flowStatComponentController.setBodyController(this);
        rolesManagementTabComponentController.setBodyController(this);
        usersManagementTabComponentController.setBodyController(this);
        usersManagementTabComponentController.initializeRolesListView(rolesManagementTabComponentController);
        this.initializeTabPane();
    }

    private void initializeTabPane() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (mainTabPane.getSelectionModel().getSelectedIndex()){
                case USERS_MANAGEMENT_TAB:
                    usersManagementTabComponentController.OnSelection();
                    break;
                case ROLES_MANAGEMENT_TAB:
                    //rolesManagementTabComponentController.initializeRolesListView(rolesManagementTabComponentController);
                    break;
                case FLOW_STAT_TAB:
                    //flowStatComponentController.initialize();
                    break;
                case FLOW_HIST_TAB:
                    //flowHistoryComponentController.initialize();
                    break;
            }
        });
    }



    public AppController getMainController() {
        return mainController;
    }

    public void updateStatistics(StatisticsDTO statistics) {
        if (statistics.getFlowStatistics().size()>0) {
            flowStatTab.setDisable(false);
            flowStatComponentController.updateBarChars(statistics);
        }
    }

    public void updateHistory(FlowsExecutionHistoryDTO historyDTO){
        if (historyDTO.getFlowExecutionDTOs().size()>0) {
            flowHistoTab.setDisable(false);
            flowHistoryComponentController.updateTable(historyDTO);
        }
    }

    public void updateFlowNames(FlowNamesDTO flowNames) {
        rolesManagementTabComponentController.enableRolesListView();
        rolesManagementTabComponentController.updateFlowNames(flowNames);
    }

    public void createRoleOnServer(List<Role> newRole) {
        mainController.createRoleOnServer(newRole);
        roleManager.addRoles(newRole);
    }

    public void updateRoles(List<Role> roles) {
        rolesManagementTabComponentController.updateRoles(roles);
    }


    public void updateOnlineUsers(Collection<UserSystemInfo> userSystemInfos) {
        usersManagementTabComponentController.updateOnlineUsers(userSystemInfos);
        rolesManagementTabComponentController.updateOnlineUsers(userSystemInfos);
    }

    public Role.RoleManager getRoleManager() {
        return roleManager;
    }

    public void updateUsersOnServer(Collection<UserSystemInfo> modifiedUsers) {
        mainController.updateUsersOnServer(modifiedUsers);
    }

    public void deleteRoleOnServer(Role selectedRole) {
        mainController.deleteRoleOnServer(selectedRole);
        roleManager.deleteRole(selectedRole);
    }

    public void triggerRolesUpdate() {
        this.createRoleOnServer(rolesManagementTabComponentController.getModifiedRoles());
    }
}
