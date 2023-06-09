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

    private IntegerProperty numOfFlowsExecuted = new SimpleIntegerProperty(0);
    private IntegerProperty numOfFlowsFinished = new SimpleIntegerProperty(0);
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
                //StatisticsDTO  sDTO = reqDispatcher.getStatisticsDTO(); -- > fetchStatistics
                //FlowsExecutionHistoryDTO hDTO = reqDispatcher.getHistoryDTO(); --> fetchHistory
                fetchRoles();
                fetchFlowNames();
                fetchOnlineUsersInfo();
                fetchHistory();
                fetchStatistics();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void fetchStatistics() {
        StatisticsDTO statisticsDTO = reqDispatcher.getStatisticsDTO();
        bodyComponentController.updateStatistics(statisticsDTO);
    }

    private void fetchHistory() {
        FlowsExecutionHistoryDTO historyDTO = reqDispatcher.getHistoryDTO();
        bodyComponentController.updateHistory(historyDTO);
    }

    public void fetchAllFlowsNames() {
        FlowNamesDTO flows = reqDispatcher.getFlowDefinitionNames();
        if (flows.size() > 0)
        {
            bodyComponentController.updateFlowNames(flows);
        }
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
//    public BodyController getBodyController() {
//        return bodyComponentController;
//    }
//    public HeaderController getHeaderController() {
//        return headerComponentController;
//    }

//    public LoadDataDTO readXML(String path) {
//        return engineController.readXML(path);
//    }
//    public FlowNamesDTO getFlowNames() {
//        return engineController.getFlowDefinitionsNames();
//    }
//
//    public void executeFlow(int flowIndex, Pair<Map, Map> valName2valType) {
//        engineController.executeFlow(flowIndex, valName2valType);
//    }
//
//    public List<FlowDefinitionDTO> getAllFlowDefinitionsData() {
//        return engineController.getAllFlowDefinitionsData();
//    }
//
//    public FlowExecutionResult getFlowExecutionResult() {
//        return lastExecutedDataCenter.getFlowExecutionResult();
//    }
//
//    public boolean isFlowExecutionInProgress() {
//        return lastExecutedDataCenter.isFlowExecutionInProgress();
//    }
//
//    public String getLastExecutedFlowName() {
//        return lastExecutedDataCenter.getLastExecutedFlowName();
//    }
//
//    public int getCurrentStepIdx() {
//        return lastExecutedDataCenter.getCurrentStepIdx();
//    }
//
//    public String getCurrentStepName() {
//        return lastExecutedDataCenter.getCurrentStepName();
//    }
//
//    public int getStepsCount(){
//        return lastExecutedDataCenter.getStepsCount();
//    }
//
//    public Map getExecutedStepsStatus(){
//        return lastExecutedDataCenter.getExecutedStepsStatus();
//    }
//
//    public Map<String,Map<String, Pair<DataDefinition, Object>>> getOutputsForAllSteps() {
//        return lastExecutedDataCenter.getOutputsForAllSteps();
//    }
//
//    public Map<String,List<String>> getAllStepsListOfLogs(){
//        return lastExecutedDataCenter.getAllStepsListOfLogs();
//    }
//
//    public Map<String, Duration> getAllStepsDuration() {
//        return lastExecutedDataCenter.getAllStepsDuration();
//    }
//
//    public Map<String, String> getAllSummaryLines() {
//        return lastExecutedDataCenter.getAllSummaryLines();
//    }
//
//    public Map getLastFlowOutputs() {
//        return lastExecutedDataCenter.getLastFlowOutputs();
//    }
}
