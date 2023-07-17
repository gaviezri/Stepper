package GUI.app;

import communication.Role;
import communication.AdminRequestsDispatcher;
import GUI.header.HeaderController;
import GUI.body.BodyController;
import communication.UserSystemInfo;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.FlowNamesDTO;
import dto.statistics.StatisticsDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AppController {
    @FXML
    private ScrollPane sceneScrollPane;
    @FXML
    private BorderPane sceneMainPane;
    @FXML
    private GridPane headerComponent;
    @FXML
    private TabPane bodyComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private BodyController bodyComponentController;
    private ScheduledExecutorService executorServiceForPollingExecutions = Executors.newSingleThreadScheduledExecutor();
    private AdminRequestsDispatcher reqDispatcher = AdminRequestsDispatcher.getInstance();
    public void initialize(){
        this.headerComponentController.setMainController(this);
        this.bodyComponentController.setMainController(this);
        initializePollingExecutions();
        initializeTabPane();
    }
    @Override
    protected void finalize() {
        reqDispatcher.putLogoutAdmin();
        executorServiceForPollingExecutions.shutdown();
    }
    public void doFinalize(){
        finalize();
    }
    private void initializeTabPane() {
        bodyComponentController.setActiveTab(BodyController.USERS_MANAGEMENT_TAB);
    }
    private void initializePollingExecutions() {
        executorServiceForPollingExecutions.scheduleAtFixedRate(() -> {
            try {
                fetchRoles();
                System.out.println("fetched roles");
                fetchFlowNames();
                System.out.println("fetched flow names");
                fetchOnlineUsersInfo();
                System.out.println("fetched online users info");
                fetchAndUpdateHistory();
                System.out.println("fetched and updated history");
                fetchStatistics();
                System.out.println("fetched statistics");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    private void fetchStatistics() {
        StatisticsDTO statisticsDTO = reqDispatcher.getStatisticsDTO();
        bodyComponentController.updateStatistics(statisticsDTO);
    }
    private void fetchAndUpdateHistory() {
        FlowsExecutionHistoryDTO historyDTO = reqDispatcher.getHistoryDTO();
        bodyComponentController.updateHistory(historyDTO);
    }
    public void filterHistoryByFilter(FlowsExecutionHistoryDTO.SortFilter filter){
        reqDispatcher.filterHistory(filter);
    }
    private void fetchOnlineUsersInfo() {
        Collection<UserSystemInfo> userSystemInfos = reqDispatcher.getOnlineUsers();
         bodyComponentController.updateOnlineUsers(userSystemInfos);
    }
    private void fetchFlowNames() {
        FlowNamesDTO flowNamesDTO = reqDispatcher.getFlowDefinitionNames();
        if (flowNamesDTO.size() > 0 && flowNamesDTO.getStatus()) {
            bodyComponentController.updateFlowNames(flowNamesDTO);
        }
    }
    private void fetchRoles() {
        List<Role> roles = reqDispatcher.getRoles();
        bodyComponentController.getRoleManager().setRolesMap(reqDispatcher.getRolesMap());
        if (roles.size() > 0) {
            bodyComponentController.updateRoles(roles);
        }
    }
    public Window getPrimaryStage() {
        return sceneMainPane.getScene().getWindow();
    }
    public void createRoleOnServer(List<Role> newRole) {
        Map updatedRoleMap = reqDispatcher.postRoles(newRole);
        bodyComponentController.getRoleManager().setRolesMap(updatedRoleMap);
    }
    public void updateUsersOnServer(Collection<UserSystemInfo> modifiedUsers) {
        reqDispatcher.putUsers(modifiedUsers);
    }
    public void setActiveTab(int rolesManagementTab) {
        bodyComponentController.setActiveTab(rolesManagementTab);
    }
    public void deleteRoleOnServer(Role selectedRole) {
        reqDispatcher.deleteRole(selectedRole);
    }
}
