package body;

import app.AppController;
import body.library.LibraryController;
import body.execution.ExecutionController;
import body.library.definition.DefinitionController;
import body.library.input.InputController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import stepper.controller.EngineController;

public class BodyController {
    private AppController mainController;
    @FXML private LibraryController flowLibComponentController;
    @FXML private ExecutionController flowExecComponentController;
    @FXML private TabPane mainTabPane;
    @FXML private Tab flowLibTab;
    @FXML private AnchorPane flowLibComponent;
    @FXML private Tab flowExecTab;
    @FXML private AnchorPane flowExecComponent;
    @FXML private Tab flowStatTab;
    @FXML private TabPane flowStatComponent;


    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        flowExecTab.setDisable(true);
        flowStatTab.setDisable(true);
      
       
      
        flowLibComponentController.setBodyController(this);
        flowLibComponentController.bindInputPaneEnablementToSelectButton();

        DefinitionController flowDefController = flowLibComponentController.getDefinitionController();
        InputController flowInputComponentController = flowLibComponentController.getInputComponentController();

        flowExecComponentController.setBodyController(this);
        flowExecComponentController.bindFakeSectionToExecutionEnablement(mainTabPane);
        flowExecComponentController.bindFlowExecutionElementsToSelectButton(flowDefController, flowInputComponentController);
        bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution();
    }

    private void bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution() {
        flowLibComponentController.getInputComponentController().getStartButton().setOnAction(event -> {
            flowExecTab.setDisable(false);
            flowStatTab.setDisable(false);
            mainTabPane.getSelectionModel().select(flowExecTab);
            EngineController.getInstance().executeFlow(flowLibComponentController.getDefinitionController().getSelectedFlowIndex(), flowLibComponentController.getInputComponentController().getValName2ValType());
        });
    }

    public void bindDefinitionTabComponents() {
        flowLibComponentController.bindLibraryTabComponents();
    }
    public AppController getMainController() {
        return mainController;
    }
    public ExecutionController getFlowExecutionController() {return flowExecComponentController;}



}
