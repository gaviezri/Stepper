package app;

import body.BodyController;
import header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import stepper.controller.EngineController;


public class AppController {
    @FXML
    private HeaderController headerController;
    @FXML
    private BodyController bodyController;
    private EngineController engineController;

    @FXML
    private ScrollPane sceneScrollPane;

    @FXML
    private BorderPane sceneMainPane;

    public void initialize() {
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
}
