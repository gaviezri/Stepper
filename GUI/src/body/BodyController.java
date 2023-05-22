package body;

import app.AppController;
import body.definition.DefinitionController;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class BodyController {
    AppController mainController;
    @FXML private DefinitionController flowDefComponentController;
    @FXML private TabPane mainTabPane;
    @FXML private Tab flowDefTab;
    @FXML private AnchorPane flowDefComponent;




    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        flowDefComponentController.setBodyController(this);

    }

    public void bindDefinitionTabComponents() {
        flowDefComponentController.bindDefinitionTabComponents();
    }

    public DefinitionController getFlowDefinitionController() {
        return flowDefComponentController;
    }
    public AppController getMainController() {
        return mainController;
    }
}
