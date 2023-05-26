package app;

import body.BodyController;
import header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import stepper.controller.EngineController;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;


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

    public void initialize(){
        engineController = EngineController.getInstance();
        bodyComponentController.setMainController(this);
        headerComponentController.setMainController(this);
        bodyComponentController.bindDefinitionTabComponents();

    }


    public EngineController getEngineController() {
        return engineController;
    }


    public Window getPrimaryStage() {
        return sceneMainPane.getScene().getWindow();
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
}
