package body;

import app.AppController;
import body.definition.DefinitionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

public class BodyController {
    AppController mainController;
    @FXML
    private DefinitionController flowDefinitionController;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab flowDefTab;

    @FXML
    private GridPane flowDefComponent;

    public void setMainController(AppController appController) {
        mainController = appController;
    }

    public void initialize() throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("definition/definitionTab.fxml"));
        flowDefComponent =  loader.load();
        flowDefinitionController = loader.getController();
        flowDefinitionController.setBodyController(this);
    }

    public DefinitionController getFlowDefinitionController() {
        return flowDefinitionController;
    }
}
