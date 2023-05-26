package body;

import app.AppController;
import body.library.LibraryController;
import body.execution.ExecutionController;
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




    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        flowExecTab.setDisable(true);
        flowLibComponentController.setBodyController(this);
        flowExecComponentController.setBodyController(this);
        flowLibComponentController.bindInputPaneEnablementToSelectButton();
        flowExecComponentController.bindFakeSectionToExecutionEnablement(mainTabPane);
        bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution();
    }

    private void bindInputExecuteButtonToExecutionTabEnablementAndInitiateExecution() {
        flowLibComponentController.getInputComponentController().getStartButton().setOnAction(event -> {
            flowExecTab.setDisable(false);
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
