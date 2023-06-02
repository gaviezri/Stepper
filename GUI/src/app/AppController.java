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
import stepper.dto.flow.FlowDefinitionDTO;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;

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
                    bodyComponentController.updateStatistics();
                }));
    }
    @Override
    protected void finalize()
    {
        executorServiceForPollingExecutions.shutdown();
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
}
