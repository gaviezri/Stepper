package body;

import app.AppController;
import body.library.LibraryController;
import body.execution.ExecutionController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class BodyController {
    private AppController mainController;
    @FXML private LibraryController flowLibComponentController;
    @FXML private ExecutionController flowExecComponentController;
    @FXML private TabPane mainTabPane;
    @FXML private Tab flowDefTab;
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
    }

    public void bindDefinitionTabComponents() {
        flowLibComponentController.bindLibraryTabComponents();
    }
    public AppController getMainController() {
        return mainController;
    }
    public ExecutionController getFlowExecutionController() {return flowExecComponentController;}



}
