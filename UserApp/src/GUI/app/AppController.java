package GUI.app;

import GUI.body.BodyController;
import GUI.login.LoginController;
import communication.Role;
import communication.UserRequestsDispatcher;
import GUI.header.HeaderController;
import communication.UserSystemInfo;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import dto.execution.progress.ExecutionProgressDTO;
import dto.flow.FlowDefinitionDTO;
import dto.statistics.StatisticsDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AppController {
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private BodyController bodyComponentController;

    @FXML
    private ScrollPane sceneScrollPane;

    @FXML
    private BorderPane sceneMainPane;

    @FXML
    private GridPane headerComponent;

    @FXML
    private TabPane bodyComponent;

    private UserRequestsDispatcher reqDispatcher = UserRequestsDispatcher.getInstance();
    private IntegerProperty numOfFlowsExecuted = new SimpleIntegerProperty(0);
    private IntegerProperty numOfFlowsFinished = new SimpleIntegerProperty(0);
    private ScheduledExecutorService executorServiceForPollingExecutions = Executors.newSingleThreadScheduledExecutor();

    private Boolean isExecutionInProgess = false;

    public void initialize(){
        this.headerComponentController.setMainController(this);
        this.bodyComponentController.setMainController(this);
        bodyComponentController.bindFlowExecutionElementsToSelectButton();
    }
    @Override
    protected void finalize() {
        UserRequestsDispatcher.getInstance().logout();
        executorServiceForPollingExecutions.shutdown();
    }
    public List<Role> getUserRolesList(){
        return reqDispatcher.getUserRolesList();
    }

    private void initializePollingExecutions() {
        executorServiceForPollingExecutions.scheduleAtFixedRate(() -> {
            try {
                updateExecutionProgress();
                updateManagerAndRoles();
                updateAccessibleFlows();
//                FlowsExecutionHistoryDTO hDTO = reqDispatcher.getHistoryDTO();
//
//                if (sDTO.getFlowStatistics().size() > 0 && hDTO.getFlowExecutionDTOs().size() > 0) {
//                    bodyComponentController.updateHistory(reqDispatcher.getHistoryDTO());
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void updateExecutionProgress() {
        if (isExecutionInProgess) {
            ExecutionProgressDTO executionProgressDTO = reqDispatcher.getExecutionProgress();
            isExecutionInProgess = executionProgressDTO.isExecutionInProgress();
            bodyComponentController.updateExecutionProgess(executionProgressDTO);

        }
    }

    private void updateAccessibleFlows() {
        List<FlowDefinitionDTO> allAccessibleFlowDefinitionsData = reqDispatcher.getAllAccessibleFlowDefinitionsData();
        if(allAccessibleFlowDefinitionsData != null ) {
            bodyComponentController.updateFlowDefinitions(allAccessibleFlowDefinitionsData);
        }
    }

    private void updateManagerAndRoles() {
        UserSystemInfo userInfo = reqDispatcher.getUsersCurrentInfo(headerComponentController.getUserName());
        headerComponentController.updateManagerAndRoleText(userInfo.isManager().toString());
    }

    public void filterHistoryByFilter(FlowsExecutionHistoryDTO.SortFilter filter){
        reqDispatcher.filterHistory(filter);
    }

    public Window getPrimaryStage() {
        return sceneMainPane.getScene().getWindow();
    }

    public synchronized IntegerProperty numOfFlowsExecutedProperty() {
        return numOfFlowsExecuted;
    }
//
    public void executeFlow(int flowIndex, Pair<Map, Map> valName2valType) {
        reqDispatcher.executeFlow(flowIndex, valName2valType);
        isExecutionInProgess = true;
    }

    private void fetchHistory() {
        FlowsExecutionHistoryDTO historyDTO = reqDispatcher.getHistory();
        bodyComponentController.updateHistory(historyDTO);
    }

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

    public void stop() {
        if (executorServiceForPollingExecutions != null) {
            executorServiceForPollingExecutions.shutdown();
            bodyComponentController.stop();
        }

    }

    public StatisticsDTO getCurrentLoadedFlowsStatisticsDetails() {
        return null;
    }

    public Map<String, Object> getLastFlowOutputs() {
        return null;
    }

    public void bindUserNameToText(TextField userNameTextField) {
        headerComponentController.bindUserNameToText(userNameTextField);
    }

    public void bindPollingToSuccessfulLogin(LoginController loginController) {
        loginController.loggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                initializePollingExecutions();
            }
        });
    }
}