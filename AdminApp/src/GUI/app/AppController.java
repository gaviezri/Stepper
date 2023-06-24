package GUI.app;

import communication.AdminRequestsDispatcher;
import GUI.header.HeaderController;
import GUI.body.BodyController;
import dto.flow.FlowDefinitionDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.statistics.StatisticsDTO;

import java.util.List;
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
    AdminRequestsDispatcher reqDispatcher = AdminRequestsDispatcher.getInstance();
    private IntegerProperty numOfFlowsExecuted = new SimpleIntegerProperty(0);
    private IntegerProperty numOfFlowsFinished = new SimpleIntegerProperty(0);
    ScheduledExecutorService executorServiceForPollingExecutions = Executors.newSingleThreadScheduledExecutor();

    public void initialize(){
        this.headerComponentController.setMainController(this);
        this.bodyComponentController.setMainController(this);
        initializePollingExecutions();
    }
    @Override
    protected void finalize() {
        executorServiceForPollingExecutions.shutdown();
        reqDispatcher.logoutAdmin();
    }
    public void doFinalize(){
        finalize();
    }

    private void initializePollingExecutions() {
        executorServiceForPollingExecutions.scheduleAtFixedRate(() -> {
            try {
                StatisticsDTO  sDTO = reqDispatcher.getStatisticsDTO();
                FlowsExecutionHistoryDTO hDTO = reqDispatcher.getHistoryDTO();
                List<String> flowNames = reqDispatcher.getFlowDefinitionNames();
//                List<String> usersName = reqDispatcher.getUsersNames();

//                if (sDTO.getFlowStatistics().size() > 0 && hDTO.getFlowExecutionDTOs().size() > 0) {
//                    bodyComponentController.updateStatistics(sDTO);
//                    bodyComponentController.updateHistory(hDTO);
//                }
                if (flowNames.size() > 0) {
                    bodyComponentController.updateFlowNames(flowNames);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }





    public Window getPrimaryStage() {
        return sceneMainPane.getScene().getWindow();
    }

    public synchronized IntegerProperty numOfFlowsExecutedProperty() {
        return numOfFlowsExecuted;
    }
    public IntegerProperty numOfFlowsFinishedProperty() {
        return numOfFlowsFinished;
    }

    public void shutdownPollingExecutions() {
        executorServiceForPollingExecutions.shutdown();
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
//
//    public void stop() {
//        if (executorServiceForPollingExecutions != null) {
//            executorServiceForPollingExecutions.shutdown();
//            bodyComponentController.stop();
//        }
//
//    }
}
