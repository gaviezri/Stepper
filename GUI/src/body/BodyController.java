package body;

import app.AppController;
import body.library.LibraryController;
import body.execution.ExecutionController;
import body.library.definition.DefinitionController;
import body.library.input.InputController;
import body.statistics.StatisticsController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

public class BodyController {
    public static final int FLOW_LIB_TAB = 0;
    public static final int FLOW_EXEC_TAB = 1;
    public static final int FLOW_STAT_TAB = 2;
    private AppController mainController;
    @FXML private LibraryController flowLibComponentController;
    @FXML private ExecutionController flowExecComponentController;
    @FXML private StatisticsController flowStatComponentController;
    @FXML private TabPane mainTabPane;
    @FXML private Tab flowLibTab;
    @FXML private AnchorPane flowLibComponent;
    @FXML private Tab flowExecTab;
    @FXML private AnchorPane flowExecComponent;
    @FXML private Tab flowStatTab;
    @FXML private TabPane flowStatComponent;

    public void updateStatistics() {
        this.flowStatComponentController.updateBarChars();
    }

    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        flowExecTab.setDisable(true);
        flowStatTab.setDisable(true);

        flowStatComponentController.setBodyController(this);

        flowLibComponentController.setBodyController(this);
        flowLibComponentController.bindInputPaneEnablementToSelectButton();
        DefinitionController flowDefController = flowLibComponentController.getDefinitionController();
        InputController flowInputComponentController = flowLibComponentController.getInputComponentController();

        flowExecComponentController.setBodyController(this);
        flowExecComponentController.bindFakeSectionToExecutionEnablement(mainTabPane);
        bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution();
    }

    private void bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution() {
        flowLibComponentController.getInputComponentController().getStartButton().setOnAction(event -> {
            flowExecTab.setDisable(false);
            flowStatTab.setDisable(false);
            mainTabPane.getSelectionModel().select(flowExecTab);

            int flowIndex = flowLibComponentController.getDefinitionController().getSelectedFlowIndex();
            Pair<Map, Map> valName2valType = flowLibComponentController.getInputComponentController().getValName2ValType();
            mainController.executeFlow(flowIndex, valName2valType);

        });
    }

    public void bindFlowExecutionElementsToSelectButton() {
        flowExecComponentController.bindFlowExecutionElementsToSelectButton(flowLibComponentController.getDefinitionController());
    }

    public void bindDefinitionTabComponents() {
        flowLibComponentController.bindLibraryTabComponents();
    }
    public AppController getMainController() {
        return mainController;
    }
    public ExecutionController getFlowExecutionController() {return flowExecComponentController;}

    public void setInputSectionToContinuation(String flowNameContinuedTo, List<Pair<String,String>> continuationDataMap) {
        flowLibComponentController.setInputSectionToContinuation(flowNameContinuedTo, continuationDataMap);
        mainTabPane.getSelectionModel().select(BodyController.FLOW_LIB_TAB);

    }
}
