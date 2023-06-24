package GUI.body;

import GUI.app.AppController;
import GUI.body.history.HistoryController;
import GUI.body.statistics.StatisticsController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import dto.execution.history.FlowsExecutionHistoryDTO;
import dto.statistics.StatisticsDTO;

public class BodyController {
    public static final int USERS_MANAGEMENT_TAB = 0;
    public static final int ROLES_MANAGEMENT_TAB = 1;
    public static final int FLOW_HIST_TAB = 2;
    public static final int FLOW_STAT_TAB = 3;
    private AppController mainController;
//    @FXML private LibraryController flowLibComponentController;
//    @FXML private ExecutionController flowExecComponentController;
    @FXML private StatisticsController flowStatComponentController;
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

//    public void updateStatistics() {
//        this.flowStatComponentController.updateBarChars();
//    }

//    public void updateHistory(Stack<FlowExecution> flowExecutionStack){
//        this.flowHistoryComponentController.updateTable(flowExecutionStack);
//    }

    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        flowHistoTab.setDisable(true);
        flowStatTab.setDisable(true);

    }

    private void initializeTab(Tab flowHistoTab) {
        flowHistoTab.setDisable(true);
        flowHistoTab.getStyleClass().add("tab");
    }

//    public LibraryController getFlowLibComponentController() {
//        return flowLibComponentController;
//    }

    private void bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution() {
//        flowLibComponentController.getInputComponentController().getStartButton().setOnAction(event -> {
            flowExecTab.setDisable(false);
            flowStatTab.setDisable(false);
            flowHistoTab.setDisable(false);
            mainTabPane.getSelectionModel().select(flowExecTab);

//            int flowIndex = flowLibComponentController.getDefinitionController().getSelectedFlowIndex();
//            Pair<Map, Map> valName2valType = flowLibComponentController.getInputComponentController().getValName2ValType();
//            getFlowExecutionController().setContinuationProperty(mainController.getAllFlowDefinitionsData().get(flowIndex));
//            mainController.executeFlow(flowIndex, valName2valType);
//
//        });
    }

    public void bindFlowExecutionElementsToSelectButton() {
//        flowExecComponentController.bindFlowExecutionElementsToSelectButton(flowLibComponentController.getDefinitionController());
    }

    public AppController getMainController() {
        return mainController;
    }

    public void updateStatistics(StatisticsDTO statistics) {
        flowStatTab.setDisable(false);
        flowStatComponentController.updateBarChars(statistics);
    }

    public void updateHistory(FlowsExecutionHistoryDTO flowsExecutionHistoryDTO){
        flowHistoTab.setDisable(false);
        flowHistoryComponentController.updateTable(flowsExecutionHistoryDTO);
    }


//    public void bindDefinitionTabComponents() {
//        flowLibComponentController.bindLibraryTabComponents();
//    }
//    public AppController getMainController() {
//        return mainController;
//    }
//    public ExecutionController getFlowExecutionController() {return flowExecComponentController;}
//
//    public void setInputSectionToContinuation(String flowNameContinuedTo, List<Pair<String,String>> continuationDataMap) {
//        flowLibComponentController.setInputSectionToContinuation(flowNameContinuedTo, continuationDataMap);
//        Platform.runLater(() -> {
//            mainTabPane.getSelectionModel().select(BodyController.FLOW_LIB_TAB);
//            flowLibComponentController.showInputComponent();
//        });
//    }
//
//    public void stop(){
//        flowExecComponentController.stop();
//    }
}
