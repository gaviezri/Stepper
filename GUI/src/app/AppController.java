package app;

import body.BodyController;
import header.HeaderController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;
import stepper.controller.EngineController;
import stepper.dd.api.DataDefinition;
import dto.flow.FlowDefinitionDTO;
import dto.flow.FlowNamesDTO;
import dto.flow.LoadDataDTO;
import dto.statistics.StatisticsDTO;
import stepper.flow.execution.FlowExecutionResult;
import stepper.flow.execution.last.executed.data.center.LastExecutedDataCenter;

import java.time.Duration;
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

    private EngineController engineController;

    private LastExecutedDataCenter lastExecutedDataCenter = LastExecutedDataCenter.getInstance();

    @FXML
    private ScrollPane sceneScrollPane;

    @FXML
    private BorderPane sceneMainPane;

    @FXML
    private GridPane headerComponent;

    @FXML
    private TabPane bodyComponent;

    private IntegerProperty numOfFlowsExecuted = new SimpleIntegerProperty(0);
    private IntegerProperty numOfFlowsFinished = new SimpleIntegerProperty(0);
    ScheduledExecutorService executorServiceForPollingExecutions = Executors.newSingleThreadScheduledExecutor();

    public void initialize(){
        executorServiceForPollingExecutions.scheduleAtFixedRate(() -> {
            numOfFlowsExecuted.set(EngineController.getInstance().getNumOfFlowsExecuted());
            numOfFlowsFinished.set(EngineController.getInstance().getNumOfFlowsFinished());
                },0, 200, TimeUnit.MILLISECONDS);
        engineController = EngineController.getInstance();
        bodyComponentController.setMainController(this);
        headerComponentController.setMainController(this);
        bodyComponentController.bindDefinitionTabComponents();
        bodyComponentController.bindFlowExecutionElementsToSelectButton();
        numOfFlowsFinished.addListener((
                (observable, oldValue, newValue) -> {
                    if(!oldValue.equals(newValue)) {
                        bodyComponentController.updateStatistics();
                        bodyComponentController.updateHistory(engineController.getFlowExecutionStack());
                    }
                }));

    }
    @Override
    protected void finalize()
    {
        executorServiceForPollingExecutions.shutdown();
    }

    public StatisticsDTO getCurrentLoadedFlowsStatisticsDetails(){
        return engineController.getInstance().getCurrentLoadedFlowsStatisticsDetails();
    }
    public EngineController getEngineController() {
        return engineController;
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

    public BodyController getBodyController() {
        return bodyComponentController;
    }
    public HeaderController getHeaderController() {
        return headerComponentController;
    }

    public LoadDataDTO readXML(String path) {
        return engineController.readXML(path);
    }
    public FlowNamesDTO getFlowNames() {
        return engineController.getFlowDefinitionsNames();
    }

    public void executeFlow(int flowIndex, Pair<Map, Map> valName2valType) {
        engineController.executeFlow(flowIndex, valName2valType);
    }

    public List<FlowDefinitionDTO> getAllFlowDefinitionsData() {
        return engineController.getAllFlowDefinitionsData();
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return lastExecutedDataCenter.getFlowExecutionResult();
    }

    public boolean isFlowExecutionInProgress() {
        return lastExecutedDataCenter.isFlowExecutionInProgress();
    }

    public String getLastExecutedFlowName() {
        return lastExecutedDataCenter.getLastExecutedFlowName();
    }

    public int getCurrentStepIdx() {
        return lastExecutedDataCenter.getCurrentStepIdx();
    }

    public String getCurrentStepName() {
        return lastExecutedDataCenter.getCurrentStepName();
    }

    public int getStepsCount(){
        return lastExecutedDataCenter.getStepsCount();
    }

    public Map getExecutedStepsStatus(){
        return lastExecutedDataCenter.getExecutedStepsStatus();
    }

    public Map<String,Map<String, Pair<DataDefinition, Object>>> getOutputsForAllSteps() {
        return lastExecutedDataCenter.getOutputsForAllSteps();
    }

    public Map<String,List<String>> getAllStepsListOfLogs(){
        return lastExecutedDataCenter.getAllStepsListOfLogs();
    }

    public Map<String, Duration> getAllStepsDuration() {
        return lastExecutedDataCenter.getAllStepsDuration();
    }

    public Map<String, String> getAllSummaryLines() {
        return lastExecutedDataCenter.getAllSummaryLines();
    }

    public Map getLastFlowOutputs() {
        return lastExecutedDataCenter.getLastFlowOutputs();
    }

    public void stop() {
        if (executorServiceForPollingExecutions != null) {
            executorServiceForPollingExecutions.shutdown();
            bodyComponentController.stop();
        }

    }

    public void setTheme(String themeSrc) {
        sceneScrollPane.getScene().getStylesheets().clear();
        sceneScrollPane.getScene().getStylesheets().add(themeSrc);
    }
}
