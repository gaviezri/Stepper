package GUI.body;

import GUI.app.AppController;
import GUI.body.execution.ExecutionController;
import GUI.body.library.LibraryController;
import GUI.body.library.definition.DefinitionController;
import GUI.body.library.input.InputController;
import GUI.body.history.HistoryController;
import dto.execution.progress.ExecutedFlowDetailsDTO;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.flow.FlowDefinitionDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import stepper.flow.execution.FlowExecution;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class BodyController {
    public static final int FLOW_LIB_TAB = 0;
    public static final int FLOW_EXEC_TAB = 1;
    public static final int FLOW_HIST_TAB = 2;
    public static final int FLOW_STAT_TAB = 3;
    private AppController mainController;
    @FXML private LibraryController flowLibComponentController;
    @FXML private ExecutionController flowExecComponentController;
    @FXML private HistoryController flowHistoryComponentController;
    @FXML private TabPane mainTabPane;
    @FXML private Tab flowLibTab;
    @FXML private AnchorPane flowLibComponent;
    @FXML private Tab flowExecTab;
    @FXML private AnchorPane flowExecComponent;
    @FXML private Tab flowStatTab;
    @FXML private TabPane flowStatComponent;
    @FXML private Tab flowHistoTab;
    @FXML private AnchorPane anchorHistory;

    public void setActiveTab(int activeTab) {
        this.mainTabPane.getSelectionModel().select(activeTab);
    }


    public void updateHistory(FlowsExecutionHistoryDTO historyDTO){
            flowHistoTab.setDisable(false);
            flowHistoryComponentController.updateTable(historyDTO);
        }



    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        initializeTab(flowExecTab);
        initializeTab(flowHistoTab);

        flowHistoryComponentController.setBodyController(this);
        flowLibComponentController.setBodyController(this);
        flowLibComponentController.bindInputPaneEnablementToSelectButton();
        DefinitionController flowDefController = flowLibComponentController.getDefinitionController();
        InputController flowInputComponentController = flowLibComponentController.getInputComponentController();

        flowExecComponentController.setBodyController(this);
        flowExecComponentController.bindFakeSectionToExecutionEnablement(mainTabPane);

        flowInputComponentController.initializeStartButton(flowExecComponentController);
        bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution();

        flowHistoryComponentController.bindInputPaneEnablementToReRunButton(flowLibComponentController.getInputComponent(),flowLibComponentController.getDefinitionComponent());
    }

    private void initializeTab(Tab flowHistoTab) {
        flowHistoTab.setDisable(true);
        flowHistoTab.getStyleClass().add("tab");
    }

    public LibraryController getFlowLibComponentController() {
        return flowLibComponentController;
    }

    public void updateFlowDefinitions(List<FlowDefinitionDTO> flowDefinitions) {
        flowLibComponentController.updateFlowDefinitions(flowDefinitions);
    }

    private void bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution() {
        flowLibComponentController.getInputComponentController().getStartButton().setOnAction(event -> {
            flowExecTab.setDisable(false);
            flowHistoTab.setDisable(false);
            mainTabPane.getSelectionModel().select(flowExecTab);
            flowExecComponentController.setActiveFlowDetails(getFlowDefinitionsDataByName(getSelectedFlowName()));
            executeFlow();
        });
    }

    private FlowDefinitionDTO getFlowDefinitionsDataByName(String selectedFlowName) {
        return flowLibComponentController.getFlowDefinitionsDataByName(selectedFlowName);
    }

    private void executeFlow() {
        mainController.executeFlow(getSelectedFlowName(), getExecutionValueAndExecutionTypes());
    }

    private Pair<Map, Map> getExecutionValueAndExecutionTypes() {
        return flowLibComponentController.getInputComponentController().getValName2ValType();
    }

    private String getSelectedFlowName(){
        return flowLibComponentController.getSelectedFlowName();
    }

    private Integer getSelectedFlowIndex(){
        return flowLibComponentController.getSelectedFlowIndex();
    }

    private FlowDefinitionDTO getFlowDefinitionsDataByIndex(int flowIndex) {
        return flowLibComponentController.getFlowDefinitionsDataByIndex(flowIndex);
    }

    public void bindFlowExecutionElementsToSelectButton() {
        flowExecComponentController.bindFlowExecutionElementsToSelectButton(flowLibComponentController.getDefinitionController());
    }

    public AppController getMainController() {
        return mainController;
    }
    public ExecutionController getFlowExecutionController() {return flowExecComponentController;}

    public void setInputSectionToContinuation(String flowNameContinuedTo, List<Pair<String,String>> continuationDataMap) {
        flowLibComponentController.setInputSectionToContinuation(flowNameContinuedTo, continuationDataMap);
        Platform.runLater(() -> {
            mainTabPane.getSelectionModel().select(BodyController.FLOW_LIB_TAB);
            flowLibComponentController.showInputComponent();
        });
    }

    public void updateExecutionProgess(ExecutedFlowDetailsDTO executionProgressDTO) {
        flowExecComponentController.updateExecutionProgess(executionProgressDTO);
    }
}
