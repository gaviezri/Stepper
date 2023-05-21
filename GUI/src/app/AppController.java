package app;

import body.BodyController;
import header.HeaderController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import stepper.controller.EngineController;
import stepper.dto.flow.FlowNamesDTO;
import stepper.dto.flow.LoadDataDTO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class AppController implements Initializable {
    @FXML
    private HeaderController headerController;
    @FXML
    private BodyController bodyController;

    private EngineController engineController;

    @FXML
    private ScrollPane sceneScrollPane;

    @FXML
    private BorderPane sceneMainPane;

    @FXML
    private GridPane headerComponent;

    @FXML
    private TabPane bodyComponent;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        engineController = EngineController.getInstance();
    }

    public EngineController getEngineController() {
        return engineController;
    }

    public void setHeaderController(HeaderController headerController) {
        this.headerController = headerController;
        headerController.setMainController(this);
    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
        bodyController.setMainController(this);
    }

    public Window getPrimaryStage() {
        return sceneMainPane.getScene().getWindow();
    }

    public BodyController getBodyController() {
        return bodyController;
    }
    public HeaderController getHeaderController() {
        return headerController;
    }

    public LoadDataDTO readXML(String path) {
        return engineController.readXML(path);
    }
    public FlowNamesDTO getFlowNames() {
        return engineController.getFlowDefinitionsNames();
    }


}
